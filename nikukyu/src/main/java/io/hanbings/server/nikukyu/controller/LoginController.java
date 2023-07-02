package io.hanbings.server.nikukyu.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.hanbings.flows.common.OAuth;
import io.hanbings.flows.common.interfaces.Access;
import io.hanbings.flows.common.interfaces.Callback;
import io.hanbings.flows.common.interfaces.Identifiable;
import io.hanbings.flows.common.interfaces.Identify;
import io.hanbings.server.nikukyu.annotation.NikukyuTokenCheck;
import io.hanbings.server.nikukyu.config.Config;
import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.data.Message;
import io.hanbings.server.nikukyu.data.Permission;
import io.hanbings.server.nikukyu.data.Token;
import io.hanbings.server.nikukyu.data.VerifyCode;
import io.hanbings.server.nikukyu.model.Account;
import io.hanbings.server.nikukyu.model.AccountAuthorization;
import io.hanbings.server.nikukyu.service.AccountService;
import io.hanbings.server.nikukyu.service.MailService;
import io.hanbings.server.nikukyu.service.OAuthProviderService;
import io.hanbings.server.nikukyu.service.TokenService;
import io.hanbings.server.nikukyu.utils.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0")
@SuppressWarnings("SpellCheckingInspection")
public class LoginController {
    // 缓存 OAuth Openid 信息与 Token 的对应关系 Token - AccountAuthorization
    static Map<String, AccountAuthorization> openids = new ConcurrentHashMap<>();
    // 缓存验证码与 Token 的对应关系 (验证码只能使用一次) Token - VerifyCode
    static Map<String, VerifyCode> verifies = new ConcurrentHashMap<>();
    final Config config;
    final MailService mails;
    final TokenService tokens;
    final AccountService accounts;
    final OAuthProviderService providers;

    @GetMapping("/login/oauth/{provider}/authorize")
    public Message<?> getOAuthAuthorize(@PathVariable String provider) {
        return Message.success(Map.of("provider", providers.authorize(provider)));
    }

    @SuppressWarnings("ConstantConditions")
    @PostMapping("/login/oauth/{provider}/callback")
    public Message<?> postOAuthCallback(
            @PathVariable String provider,
            @RequestParam("code") String code,
            @RequestParam("state") String state
    ) {
        // 获取 OAuth 信息
        OAuth<? extends Access, ? extends Access.Wrong> client = providers.provider(provider);

        // 获取 Token
        @SuppressWarnings("rawtypes")
        Callback callback = client.token(code, state, String.format("%s/login/oauth/%s/callback", config.getSite(), provider));
        if (callback == null) return Message.Messages.BAD_REQUEST;
        if (callback.data() == null && !callback.success())
            return Message.badRequest(((Access.Wrong) callback.wrong()).error());
        if (callback.data() == null) return Message.Messages.BAD_REQUEST;

        // 获取用户信息
        if (!(client instanceof @SuppressWarnings("rawtypes")Identifiable identifiable))
            return Message.Messages.BAD_REQUEST;
        @SuppressWarnings("rawtypes")
        Callback identify = identifiable.identify(callback.token());
        if (identify == null) return Message.Messages.BAD_REQUEST;
        if (identify.data() == null && !identify.success()) return Message.badRequest(identify.wrong());
        if (identify.data() == null) return Message.Messages.BAD_REQUEST;

        // 邮箱
        String oepnid = ((Identify) identify.data()).openid();
        String email = ((Identify) identify.data()).email();

        // 获取当前的 openid 是否已经绑定了账号
        AccountAuthorization authorization = accounts.getAccountAuthorizationWithOpenid(oepnid);

        // OAuth 未被注册但邮箱已被占用
        if (authorization == null && email != null) {
            return new Message<>(Message.ReturnCode.MAIL_EXIST, "该邮箱地址已被注册", Map.of("email", email));
        }

        // 如果已经存入系统则直接返回 Token
        if (authorization != null) {
            // 如果存在则从 authorizations 中获取 auid
            Account account = accounts.getAccountWithAuid(authorization.auid());

            // 创建 token
            Token token = tokens.signature(
                    account.auid(),
                    System.currentTimeMillis() + TokenService.Expire.WEEK,
                    AccessType.all()
            );

            return Message.success(Map.of("token", token.token()));
        }

        // 如果还没存在则创建一个新的 AccountAuthorization 然后返回一个仅有发送 email 权限的 token 要求用户验证
        authorization = accounts.createAccountAuthorization(new AccountAuthorization(
                UUID.randomUUID(),
                System.currentTimeMillis(),
                null,
                provider,
                oepnid
        ));

        // 创建 token
        Token token = tokens.signature(
                null,
                System.currentTimeMillis() + TokenService.Expire.MINUTE * 10,
                List.of(AccessType.OAUTH_EMAIL_VERIFY)
        );

        // 放入缓存
        openids.put(token.token(), authorization);

        return Message.success(Map.of("token", token));
    }

    @PostMapping("/login/verify/email")
    @SaCheckPermission(Permission.OAUTH_EMAIL_VERIFY)
    @NikukyuTokenCheck(access = {AccessType.OAUTH_EMAIL_VERIFY})
    public Message<?> verifyEmail(
            @RequestParam("email") String email,
            @RequestHeader("Authorization") String token
    ) {
        // 裁剪 Token
        VerifyCode verify = new VerifyCode(
                RandomUtils.strings(6),
                System.currentTimeMillis() + TokenService.Expire.MINUTE * 5,
                email
        );

        // 发送邮件
        mails.send(email, "验证码", verify.code());

        // 放入缓存
        verifies.put(token.substring(token.indexOf("Bearer ") + 7), verify);

        return Message.Messages.SUCCESS;
    }

    @PostMapping("/login/oauth/token")
    @SaCheckPermission(Permission.EMAIL_VERIFY)
    @NikukyuTokenCheck(access = {AccessType.EMAIL_VERIFY})
    public Message<?> getToken(
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "code", required = false) String code,
            @RequestHeader("Authorization") String token
    ) {
        String bearer = token.substring(token.indexOf("Bearer ") + 7);

        // 查询
        VerifyCode verify = verifies.get(bearer);

        // 清除 VerifyCode 缓存
        verifies.remove(bearer);

        // 验证不存在
        if (verify == null) return new Message<>(Message.ReturnCode.MAIL_VERIFY_INVALID, "验证不存在", null);
        // 查询是否超时
        if (verify.expire() < System.currentTimeMillis())
            return new Message<>(Message.ReturnCode.MAIL_VERIFY_INVALID, "验证码已过期", null);
        // 验证码是否正确
        if (!verify.code().equals(code))
            return new Message<>(Message.ReturnCode.MAIL_VERIFY_INVALID, "验证码错误", null);

        // 验证成功
        // 创建 Account
        Account account = new Account(
                UUID.randomUUID(),
                System.currentTimeMillis(),
                true,
                RandomUtils.strings(8), "", "", "", "", email
        );

        // 获取 Authorization
        AccountAuthorization temp = openids.get(bearer);

        // 更新 Authorization
        AccountAuthorization authorization = new AccountAuthorization(
                temp.auid(),
                temp.create(),
                account.auid(),
                temp.provider(),
                temp.openid()
        );

        // 清除 Authorization 缓存
        openids.remove(bearer);

        // 存储
        accounts.createAccount(account);
        accounts.createAccountAuthorization(authorization);

        // 签发 Token
        return Message.success(
                Map.of(
                        "token",
                        tokens.signature(
                                account.auid(),
                                System.currentTimeMillis() + TokenService.Expire.WEEK,
                                AccessType.all()
                        ).token()
                )
        );
    }
}
