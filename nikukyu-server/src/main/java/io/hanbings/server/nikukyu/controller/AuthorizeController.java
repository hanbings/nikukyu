package io.hanbings.server.nikukyu.controller;

import io.hanbings.server.nikukyu.annotation.NikukyuTokenCheck;
import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.data.Message;
import io.hanbings.server.nikukyu.data.OAuthAuthorizeFlow;
import io.hanbings.server.nikukyu.data.Token;
import io.hanbings.server.nikukyu.exception.OAuthAuthorizeException;
import io.hanbings.server.nikukyu.model.Account;
import io.hanbings.server.nikukyu.model.AccountOAuth;
import io.hanbings.server.nikukyu.model.OAuth;
import io.hanbings.server.nikukyu.model.OAuthClient;
import io.hanbings.server.nikukyu.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0")
@SuppressWarnings("SpellCheckingInspection")
public class AuthorizeController {
    final TokenService tokenService;
    final OAuthService oAuthService;
    final AccountService accountService;
    final LocationService locationService;
    final AuthorizeService authorizeService;

    @PostMapping("/oauth/authorize")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_AUTHORIZE})
    public Message<?> authorize(
            @RequestParam("scope") String scope,
            @RequestParam("state") String state,
            @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri,
            // @RequestParam("response_type") String responseType,
            @RequestHeader("Authorization") String token
    ) {
        Token t = tokenService.get(token.substring(7));
        Account account = accountService.getAccountWithAuid(t.belong());

        // 根据 client_id 获取 oauth client 信息
        OAuthClient client = oAuthService.getOAuthClientWithOcid(clientId);
        if (client == null) {
            throw new OAuthAuthorizeException(
                    Message.ReturnCode.OAUTH_CLIENT_ID_INVALID,
                    Message.Messages.OAUTH_CLIENT_ID_INVALID
            );
        }

        // 获取 oauth 信息
        OAuth oauth = oAuthService.getOAuthWithOuid(client.ouid());
        if (oauth == null) {
            throw new OAuthAuthorizeException(
                    Message.ReturnCode.OAUTH_CLIENT_ID_INVALID,
                    Message.Messages.OAUTH_CLIENT_ID_INVALID
            );
        }

        // 检验 redirect_uri 是否合法
        try {
            URL url = new URL(redirectUri);
            // 转为 List<URL> 然后比较 getHost
            List<URL> urls = new ArrayList<>();

            for (String s : oauth.redirect()) {
                urls.add(new URL(s));
            }

            boolean matched = urls.stream().anyMatch(u -> u.getHost().equals(url.getHost()));

            if (matched) {
                throw new OAuthAuthorizeException(
                        Message.ReturnCode.OAUTH_CLIENT_REDIRECT_URI_NOT_MATCH,
                        Message.Messages.OAUTH_CLIENT_REDIRECT_URI_NOT_MATCH
                );
            }
        } catch (Exception e) {
            throw new OAuthAuthorizeException(
                    Message.ReturnCode.OAUTH_CLIENT_REDIRECT_URI_INVALID,
                    Message.Messages.OAUTH_CLIENT_REDIRECT_URI_INVALID
            );
        }

        // scpoe 检查
        List<AccessType> access = AccessType.parse(scope);
        if (access == null) access = oauth.access();
        if (new HashSet<>(oauth.access()).containsAll(access)) {
            throw new OAuthAuthorizeException(
                    Message.ReturnCode.OAUTH_CLIENT_SCOPE_INVALID,
                    Message.Messages.OAUTH_CLIENT_SCOPE_INVALID
            );
        }

        // 保存授权码
        String code = authorizeService.createOAuthAuthorizationFlow(account, oauth, client, access, state);

        return Message.success(
                new HashMap<>() {{
                    put("code", code);
                    if (state != null) put("state", state);
                }}
        );
    }

    @PostMapping("/oauth/token")
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
                Objects.equals(state, oAuthAuthorizeFlow.state())) {
            return new HashMap<>() {{
                put("error_code", String.valueOf(Message.ReturnCode.OAUTH_STATE_INVALID));
                put("error_message", Message.Messages.OAUTH_STATE_INVALID);
                if (state != null) put("state", state);
            }};
        }

        // 检查 client 与 secret
        if (!(Objects.equals(oAuthAuthorizeFlow.client().ouid(), clientId) &&
                Objects.equals(oAuthAuthorizeFlow.client().secret(), clientSecret))) {
            return new HashMap<>() {{
                put("error_code", String.valueOf(Message.ReturnCode.OAUTH_CLIENT_SECRET_INVALID));
                put("error_message", Message.Messages.OAUTH_CLIENT_SECRET_INVALID);
                if (state != null) put("state", state);
            }};
        }


        @SuppressWarnings("unused") AccountOAuth oAuth = accountService.createAccountOAuth(
                oAuthAuthorizeFlow.account().auid(),
                oAuthAuthorizeFlow.oauth().ouid(),
                oAuthAuthorizeFlow.access()
        );

        Token access = tokenService.signature(
                oAuthAuthorizeFlow.account().auid(),
                TokenService.Expire.WEEK,
                oAuthAuthorizeFlow.access()
        );

        return new HashMap<>() {{
            put("access_token", access.token());
            put("expire_in", String.valueOf(access.expire()));
            put("token_type", "Bearer");
            put("scope", AccessType.parse(oAuthAuthorizeFlow.access()));
            if (state != null) put("state", state);
        }};
    }
}
