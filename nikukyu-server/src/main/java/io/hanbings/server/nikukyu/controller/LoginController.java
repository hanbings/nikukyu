package io.hanbings.server.nikukyu.controller;

import io.hanbings.flows.common.OAuth;
import io.hanbings.flows.common.interfaces.Access;
import io.hanbings.flows.common.interfaces.Callback;
import io.hanbings.flows.common.interfaces.Identifiable;
import io.hanbings.flows.common.interfaces.Identify;
import io.hanbings.server.nikukyu.annotation.NikukyuTokenCheck;
import io.hanbings.server.nikukyu.config.Config;
import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.data.MailVerifyFlow;
import io.hanbings.server.nikukyu.data.Message;
import io.hanbings.server.nikukyu.data.Token;
import io.hanbings.server.nikukyu.exception.EmailVerifyException;
import io.hanbings.server.nikukyu.exception.OAuthProviderException;
import io.hanbings.server.nikukyu.model.Account;
import io.hanbings.server.nikukyu.model.AccountAuthorization;
import io.hanbings.server.nikukyu.service.*;
import io.hanbings.server.nikukyu.utils.FormatCheck;
import io.hanbings.server.nikukyu.utils.RandomUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0")
@SuppressWarnings("SpellCheckingInspection")
public class LoginController {
    final Config config;
    final MailService mailService;
    final TokenService tokenService;
    final LoginService loginService;
    final AccountService accountService;
    final LocationService locationService;

    @GetMapping("/login/oauth/{provider}/authorize")
    public Message<?> getOAuthAuthorize(@PathVariable String provider) {
        return Message.success(Map.of("provider", loginService.getOAuthLoginAccountAuthorize(provider)));
    }

    @SuppressWarnings("ConstantConditions")
    @PostMapping("/login/oauth/{provider}/callback")
    public Message<?> postOAuthCallback(
            @PathVariable String provider,
            @RequestParam("code") String code,
            @RequestParam("state") String state
    ) {
        // 获取 OAuth 信息
        OAuth<? extends Access, ? extends Access.Wrong> client = loginService.getOAuthProviders(provider);

        // 获取 Token
        @SuppressWarnings("rawtypes") Callback callback = client.token(
                code,
                state,
                String.format("%s/login/oauth/%s/callback", config.getSite(), provider)
        );

        if (callback == null) {
            throw new OAuthProviderException(
                    Message.ReturnCode.OAUTH_PROVIDER_INVALID,
                    Message.Messages.OAUTH_PROVIDER_INVALID
            );
        }

        if (callback.throwable() != null) {
            throw new OAuthProviderException(
                    Message.ReturnCode.OAUTH_PROVIDER_REQUEST_NETWORK_EXCEPTION,
                    Message.Messages.OAUTH_PROVIDER_REQUEST_NETWORK_EXCEPTION
            );
        }

        if (callback.data() == null && !callback.success() && callback.wrong() != null) {
            throw new OAuthProviderException(
                    Message.ReturnCode.OAUTH_PROVIDER_REQUEST_EXCEPTION,
                    ((Access.Wrong) callback.wrong()).error()
            );
        }

        if (callback.data() == null) {
            throw new OAuthProviderException(
                    Message.ReturnCode.OAUTH_PROVIDER_INVALID,
                    Message.Messages.OAUTH_PROVIDER_INVALID
            );
        }

        // 获取用户信息
        if (!(client instanceof @SuppressWarnings("rawtypes")Identifiable identifiable)) {
            throw new OAuthProviderException(
                    Message.ReturnCode.OAUTH_PROVIDER_INVALID,
                    Message.Messages.OAUTH_PROVIDER_INVALID
            );
        }

        @SuppressWarnings("rawtypes") Callback identify = identifiable.identify(callback.token());
        if (identify == null) {
            throw new OAuthProviderException(
                    Message.ReturnCode.OAUTH_PROVIDER_INVALID,
                    Message.Messages.OAUTH_PROVIDER_INVALID
            );
        }

        if (identify.throwable() != null) {
            throw new OAuthProviderException(
                    Message.ReturnCode.OAUTH_PROVIDER_REQUEST_NETWORK_EXCEPTION,
                    Message.Messages.OAUTH_PROVIDER_REQUEST_NETWORK_EXCEPTION
            );
        }

        if (identify.data() == null && !identify.success() && identify.wrong() != null) {
            throw new OAuthProviderException(
                    Message.ReturnCode.OAUTH_PROVIDER_REQUEST_EXCEPTION,
                    identify.wrong().toString()
            );
        }

        if (identify.data() == null) {
            throw new OAuthProviderException(
                    Message.ReturnCode.OAUTH_PROVIDER_INVALID,
                    Message.Messages.OAUTH_PROVIDER_INVALID
            );
        }

        // 邮箱
        String oepnid = ((Identify) identify.data()).openid();
        String email = ((Identify) identify.data()).email();

        // 获取当前的 openid 是否已经绑定了账号
        Account account = accountService.getAccountWithEmail(email);
        AccountAuthorization authorization = accountService.getAccountAuthorizationWithOpenid(oepnid);

        // OAuth 未被注册但邮箱已被占用
        if (account != null && authorization == null) {
            throw new EmailVerifyException(
                    Message.ReturnCode.MAIL_EXIST,
                    Message.Messages.EMAIL_EXIST,
                    Map.of("email", email)
            );
        }

        // 如果已经存入系统则直接返回 Token
        if (authorization != null) {
            // 如果存在则从 authorizations 中获取 auid
            account = accountService.getAccountWithAuid(authorization.auid());

            // 创建 token
            Token token = tokenService.signature(
                    account.auid(),
                    System.currentTimeMillis() + TokenService.Expire.WEEK,
                    AccessType.all()
            );

            return Message.success(Map.of("token", token));
        }

        // 如果还没存在则创建一个新的 AccountAuthorization 然后返回一个仅有发送 email 权限的 token 要求用户验证
        authorization = new AccountAuthorization(RandomUtils.uuid(), System.currentTimeMillis(), null, provider, oepnid);

        // 创建 token
        Token token = tokenService.signature(
                null,
                System.currentTimeMillis() + TokenService.Expire.MINUTE * 10,
                List.of(AccessType.OAUTH_EMAIL_VERIFY)
        );

        @SuppressWarnings("unused") MailVerifyFlow flow =
                loginService.createMailVerifyFlow(token, email, authorization, (Identify) identify.data());

        return Message.success(Map.of("token", token, "email", email == null ? "" : email));
    }

    @GetMapping("/login/verify/token")
    public Message<?> token() {
        return Message.success(
                tokenService.signature(
                        null,
                        System.currentTimeMillis() + TokenService.Expire.MINUTE * 5,
                        List.of(AccessType.EMAIL_VERIFY)
                )
        );
    }

    @SneakyThrows
    @PostMapping("/login/email/verify")
    @NikukyuTokenCheck(
            access = {AccessType.OAUTH_EMAIL_VERIFY, AccessType.EMAIL_VERIFY},
            checkAccessOr = true
    )
    public Message<?> verifyEmail(@RequestParam("email") String email, @RequestHeader("Authorization") String token) {
        MailVerifyFlow flow = loginService.getVerifyFlow(tokenService.parse(token));

        // 检查参数
        if (flow == null) {
            flow = loginService.createMailVerifyFlow(tokenService.parse(token), email, null, null);
        }

        // 对照 email
        if (!Objects.equals(email, flow.email())) {
            loginService.createMailVerifyFlow(tokenService.parse(token), email, flow.accountAuthorization(), flow.identify());
        }

        if (!FormatCheck.checkEmail(email)) {
            throw new EmailVerifyException(
                    Message.ReturnCode.EMAIL_FORMAT_INVALID,
                    Message.Messages.EMAIL_FORMAT_INVALID,
                    Map.of("email", email)
            );
        }

        // 发送邮件
        mailService.sendVerifyMail(flow.email(), flow.code());

        return Message.success(Map.of("email", email));
    }

    @PostMapping("/login/token")
    @NikukyuTokenCheck(
            access = {AccessType.OAUTH_EMAIL_VERIFY, AccessType.EMAIL_VERIFY},
            checkAccessOr = true
    )
    public Message<?> getToken(
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "code", required = false) String code,
            @RequestHeader("Authorization") String bearer
    ) {
        Token token = tokenService.parse(bearer);

        // 查询
        MailVerifyFlow flow = loginService.getVerifyFlow(token);

        // 验证不存在
        if (flow == null) {
            throw new EmailVerifyException(
                    Message.ReturnCode.MAIL_VERIFY_INVALID,
                    Message.Messages.EMAIL_VERIFY_INVALID,
                    Map.of("token", token.token())
            );
        }
        // 查询是否超时
        if (flow.expire() < System.currentTimeMillis()) {
            throw new EmailVerifyException(
                    Message.ReturnCode.MAIL_VERIFY_INVALID,
                    Message.Messages.EMAIL_VERIFY_INVALID,
                    Map.of("token", token.token())
            );
        }

        // 验证码是否正确
        if (!flow.code().equals(code)) {
            throw new EmailVerifyException(
                    Message.ReturnCode.MAIL_VERIFY_INVALID,
                    Message.Messages.EMAIL_VERIFY_INVALID,
                    Map.of("token", token.token())
            );
        }

        // 信息
        Identify identify = flow.identify();

        // 验证成功
        // 创建 Account
        Account account = accountService.createAccount(
                true,
                RandomUtils.strings(8),
                identify == null ? "" : identify.nickname(),
                identify == null ? "" : identify.avatar(),
                "",
                "",
                email
        );

        if (flow.accountAuthorization() != null) {
            // 获取 Authorization
            AccountAuthorization temp = flow.accountAuthorization();

            // 更新 Authorization
            @SuppressWarnings("unused") AccountAuthorization authorization = accountService.createAccountAuthorization(
                    account.auid(),
                    temp.provider(),
                    temp.openid()
            );
        }

        // 签发 Token
        return Message.success(
                tokenService.signature(
                        account.auid(),
                        System.currentTimeMillis() + TokenService.Expire.WEEK,
                        AccessType.all()
                )
        );
    }
}
