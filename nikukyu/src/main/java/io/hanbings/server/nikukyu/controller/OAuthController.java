package io.hanbings.server.nikukyu.controller;

import io.hanbings.server.nikukyu.annotation.NikukyuTokenCheck;
import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.data.Account;
import io.hanbings.server.nikukyu.data.AccountOAuth;
import io.hanbings.server.nikukyu.data.OAuth;
import io.hanbings.server.nikukyu.data.OAuthClient;
import io.hanbings.server.nikukyu.model.Authorize;
import io.hanbings.server.nikukyu.model.Message;
import io.hanbings.server.nikukyu.model.Token;
import io.hanbings.server.nikukyu.service.AccountService;
import io.hanbings.server.nikukyu.service.OAuthAuthorizeService;
import io.hanbings.server.nikukyu.service.OAuthService;
import io.hanbings.server.nikukyu.service.TokenService;
import io.hanbings.server.nikukyu.utils.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0")
@SuppressWarnings("SpellCheckingInspection")
public class OAuthController {
    final TokenService tokens;
    final AccountService accounts;
    final OAuthService oauths;
    final OAuthAuthorizeService authorizes;

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
        Token t = tokens.get(token.substring(7));
        Account account = accounts.getAccountWithAuid(t.belong());

        // 根据 client_id 获取 oauth client 信息
        OAuthClient client = oauths.getOAuthClientWithOcid(UUID.fromString(clientId));
        if (client == null)
            return new Message<>(Message.ReturnCode.OAUTH_CLIENT_ID_INVALID, "client_id 无效 请联系应用开发者", null);

        // 获取 oauth 信息
        OAuth oauth = oauths.getOAuthWithOuid(client.ouid());
        if (oauth == null) return Message.serverError(null);

        // 检验 redirect_uri 是否合法
        // FIXME
        try {
            URL url = new URL(redirectUri);
            boolean matched = oauth.redirect().stream().noneMatch(s -> s.equals(url.getHost()));
            if (matched)
                return new Message<>(Message.ReturnCode.OAUTH_CLIENT_REDIRECT_URI_NOT_MATCH, "redirect_uri 匹配错误 请联系应用开发者", null);
        } catch (Exception e) {
            return new Message<>(Message.ReturnCode.OAUTH_CLIENT_REDIRECT_URI_INVALID, "redirect_uri 错误 请联系应用开发者", null);
        }

        // scpoe 检查
        List<AccessType> access = AccessType.parse(scope);
        if (access == null) access = oauth.access();
        if (new HashSet<>(oauth.access()).containsAll(access))
            return new Message<>(Message.ReturnCode.OAUTH_CLIENT_SCOPE_INVALID, "scope 范围错误 请联系应用开发者", null);

        // 鉴权临时缓存
        Authorize authorize = new Authorize(
                account,
                oauth,
                client,
                access,
                state
        );

        // 创建授权码
        String code = RandomUtils.uuid();

        // 保存授权码
        authorizes.create(code, authorize);

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
        Authorize authorize = authorizes.get(code);

        if (authorize == null) {
            return new HashMap<>() {{
                put("error_code", String.valueOf(Message.ReturnCode.OAUTH_AUTHORIZE_CODE_INVALID));
                put("error_message", "授权码无效");
                if (state != null) put("state", state);
            }};
        }

        // 检查 state
        if ((state == null && authorize.state() == null) || Objects.equals(state, authorize.state())) {
            return new HashMap<>() {{
                put("error_code", String.valueOf(Message.ReturnCode.OAUTH_STATE_INVALID));
                put("error_message", "state 无效");
                if (state != null) put("state", state);
            }};
        }

        // 检查 client 与 secret
        if (!(Objects.equals(authorize.client().ouid(), UUID.fromString(clientId)) && Objects.equals(authorize.client().secret(), clientSecret))) {
            return new HashMap<>() {{
                put("error_code", String.valueOf(Message.ReturnCode.OAUTH_CLIENT_SECRET_INVALID));
                put("error_message", "secret 匹配失败");
                if (state != null) put("state", state);
            }};
        }

        accounts.createAccountOAuth(new AccountOAuth(
                UUID.randomUUID(),
                System.currentTimeMillis(),
                authorize.account().auid(),
                authorize.oauth().ouid(),
                authorize.access()
        ));

        Token access = tokens.signature(authorize.account().auid(), TokenService.Expire.WEEK, authorize.access());

        return new HashMap<>() {{
            put("access_token", access.token());
            put("expire_in", String.valueOf(access.expire()));
            put("token_type", "Bearer");
            put("scope", AccessType.parse(authorize.access()));
            if (state != null) put("state", state);
        }};
    }
}
