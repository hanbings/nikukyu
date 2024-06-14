package io.hanbings.nikukyu.server.controller;

import io.hanbings.nikukyu.server.annotation.NikukyuPermissionCheck;
import io.hanbings.nikukyu.server.data.Message;
import io.hanbings.nikukyu.server.exception.NikukyuException;
import io.hanbings.nikukyu.server.model.Account;
import io.hanbings.nikukyu.server.model.AccountOAuth;
import io.hanbings.nikukyu.server.model.OAuth;
import io.hanbings.nikukyu.server.model.OAuthClient;
import io.hanbings.nikukyu.server.security.Header;
import io.hanbings.nikukyu.server.security.OAuthAuthorizeFlow;
import io.hanbings.nikukyu.server.security.Permission;
import io.hanbings.nikukyu.server.security.Token;
import io.hanbings.nikukyu.server.service.AccountService;
import io.hanbings.nikukyu.server.service.AuthorizeService;
import io.hanbings.nikukyu.server.service.OAuthService;
import io.hanbings.nikukyu.server.service.TokenService;
import io.hanbings.nikukyu.server.utils.RandomUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/oauth")
@SuppressWarnings("SpellCheckingInspection")
public class AuthorizeController {
    final TokenService tokenService;
    final OAuthService oAuthService;
    final HttpServletRequest request;
    final AccountService accountService;
    final AuthorizeService authorizeService;

    @GetMapping("/client/{client_id}")
    @NikukyuPermissionCheck(access = {}, requiredLogin = false)
    public Object getClient(@PathVariable(value = "client_id") String clientId) {
        OAuthClient client = oAuthService.getOAuthClient(clientId);

        return oAuthService.getOAuth(client.createdBy());
    }

    @PostMapping("/authorize")
    @NikukyuPermissionCheck(access = {Permission.ACCOUNT_OAUTH_CREATE})
    public Object authorize(
            @RequestParam("scope") String scope,
            @RequestParam("state") String state,
            @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri
            // @RequestParam("response_type") String responseType
    ) {
        Account account = accountService.getAccount(request.getHeader(Header.ACCOUNT));

        // 根据 client_id 获取 oauth client 信息
        OAuthClient client = oAuthService.getOAuthClient(clientId);
        if (client == null) {
            throw new NikukyuException(
                    RandomUtils.uuid(),
                    Message.ReturnCode.OAUTH_CLIENT_ID_INVALID,
                    Message.Messages.OAUTH_CLIENT_ID_INVALID
            );
        }

        // 获取 oauth 信息
        OAuth oauth = oAuthService.getOAuth(client.createdBy());
        if (oauth == null) {
            throw new NikukyuException(
                    RandomUtils.uuid(),
                    Message.ReturnCode.OAUTH_CLIENT_ID_INVALID,
                    Message.Messages.OAUTH_CLIENT_ID_INVALID
            );
        }

        // 检验 redirect_uri 是否合法
        try {
            URI url = new URI(redirectUri);
            // 转为 List<URL> 然后比较 getHost
            List<URI> urls = new ArrayList<>();

            for (String s : oauth.redirect()) {
                urls.add(new URI(s));
            }

            boolean matched = urls.stream().anyMatch(u -> u.getHost().equals(url.getHost()));

            if (!matched) {
                throw new NikukyuException(
                        RandomUtils.uuid(),
                        Message.ReturnCode.OAUTH_CLIENT_REDIRECT_URI_NOT_MATCH,
                        Message.Messages.OAUTH_CLIENT_REDIRECT_URI_NOT_MATCH
                );
            }
        } catch (Exception e) {
            throw new NikukyuException(
                    RandomUtils.uuid(),
                    Message.ReturnCode.OAUTH_CLIENT_REDIRECT_URI_INVALID,
                    Message.Messages.OAUTH_CLIENT_REDIRECT_URI_INVALID
            );
        }

        // scpoe 检查
        Set<String> access = Arrays.stream(scope.split(","))
                .map(String::trim)
                .map(s -> s.replaceAll("\\s", ""))
                .collect(Collectors.toSet());
        if (access.isEmpty()) access = oauth.access();
        if (!new HashSet<>(oauth.access()).containsAll(access)) {
            throw new NikukyuException(
                    RandomUtils.uuid(),
                    Message.ReturnCode.OAUTH_CLIENT_SCOPE_INVALID,
                    Message.Messages.OAUTH_CLIENT_SCOPE_INVALID
            );
        }

        // 保存授权码
        String code = authorizeService.createOAuthAuthorizationFlow(account, oauth, client, access, state);

        return new HashMap<>() {{
            put("code", code);
            if (state != null) put("state", state);
        }};
    }

    @PostMapping("/token")
    @NikukyuPermissionCheck(access = {Permission.ACCOUNT_OAUTH_CREATE})
    public Map<String, String> token(
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("code") String code,
            @RequestParam("state") String state
    ) {
        OAuthAuthorizeFlow oAuthAuthorizeFlow = authorizeService.getOAuthAuthorizationFlow(code);

        if (oAuthAuthorizeFlow == null) {
            return new HashMap<>() {{
                put("error_code", String.valueOf(Message.ReturnCode.OAUTH_AUTHORIZE_CODE_INVALID));
                put("error_message", Message.Messages.OAUTH_AUTHORIZE_CODE_INVALID);
                if (state != null) put("state", state);
            }};
        }

        // 检查 state
        if ((state == null && oAuthAuthorizeFlow.state() == null) ||
                !Objects.equals(state, oAuthAuthorizeFlow.state())) {
            return new HashMap<>() {{
                put("error_code", String.valueOf(Message.ReturnCode.OAUTH_STATE_INVALID));
                put("error_message", Message.Messages.OAUTH_STATE_INVALID);
                if (state != null) put("state", state);
            }};
        }

        // 检查 client 与 secret
        if (!oAuthService.checkSecret(clientId, clientSecret)) {
            return new HashMap<>() {{
                put("error_code", String.valueOf(Message.ReturnCode.OAUTH_CLIENT_SECRET_INVALID));
                put("error_message", Message.Messages.OAUTH_CLIENT_SECRET_INVALID);
                put("state", state);
            }};
        }


        @SuppressWarnings("unused") AccountOAuth oAuth = accountService.createAccountOAuth(
                oAuthAuthorizeFlow.account().accountId(),
                oAuthAuthorizeFlow.oauth().oauthId(),
                oAuthAuthorizeFlow.access()
        );

        Token access = tokenService.signature(
                oAuthAuthorizeFlow.account().accountId(),
                TokenService.Expire.WEEK,
                oAuthAuthorizeFlow.access()
        );

        return new HashMap<>() {{
            put("access_token", access.token());
            put("expire_in", String.valueOf(access.expire()));
            put("token_type", "Bearer");
            put("scope", String.join(",", oAuthAuthorizeFlow.access()));
            put("state", state);
        }};
    }
}
