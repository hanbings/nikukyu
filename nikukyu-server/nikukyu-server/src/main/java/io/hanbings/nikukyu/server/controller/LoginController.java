package io.hanbings.nikukyu.server.controller;

import io.hanbings.flows.common.OAuth;
import io.hanbings.flows.common.interfaces.Access;
import io.hanbings.flows.common.interfaces.Callback;
import io.hanbings.flows.common.interfaces.Identifiable;
import io.hanbings.flows.common.interfaces.Identify;
import io.hanbings.nikukyu.common.data.Message;
import io.hanbings.nikukyu.common.exception.NikukyuException;
import io.hanbings.nikukyu.common.model.Account;
import io.hanbings.nikukyu.common.model.AccountAuthorization;
import io.hanbings.nikukyu.common.security.MailVerifyFlow;
import io.hanbings.nikukyu.common.security.Permission;
import io.hanbings.nikukyu.common.security.Role;
import io.hanbings.nikukyu.common.security.Token;
import io.hanbings.nikukyu.common.utils.FormatUtils;
import io.hanbings.nikukyu.common.utils.RandomUtils;
import io.hanbings.nikukyu.common.utils.TimeUtils;
import io.hanbings.nikukyu.server.annotation.NikukyuPermissionCheck;
import io.hanbings.nikukyu.server.config.Config;
import io.hanbings.nikukyu.server.service.AccountService;
import io.hanbings.nikukyu.server.service.LoginService;
import io.hanbings.nikukyu.server.service.MailService;
import io.hanbings.nikukyu.server.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
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

    @GetMapping("/login/oauth/{provider}/authorize")
    public Object getOAuthAuthorize(@PathVariable String provider) {
        return Map.of("provider", loginService.getOAuthLoginAccountAuthorize(provider));
    }

    @SuppressWarnings("all")
    @PostMapping("/login/oauth/{provider}/callback")
    public Object postOAuthCallback(
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
            throw new NikukyuException(
                    RandomUtils.uuid(),
                    Message.ReturnCode.OAUTH_PROVIDER_INVALID,
                    Message.Messages.OAUTH_PROVIDER_INVALID
            );
        }

        if (callback.throwable() != null) {
            throw new NikukyuException(
                    RandomUtils.uuid(),
                    Message.ReturnCode.OAUTH_PROVIDER_REQUEST_NETWORK_EXCEPTION,
                    Message.Messages.OAUTH_PROVIDER_REQUEST_NETWORK_EXCEPTION
            );
        }

        if (callback.data() == null && !callback.success() && callback.wrong() != null) {
            throw new NikukyuException(
                    RandomUtils.uuid(),
                    Message.ReturnCode.OAUTH_PROVIDER_REQUEST_EXCEPTION,
                    ((Access.Wrong) callback.wrong()).error()
            );
        }

        if (callback.data() == null) {
            throw new NikukyuException(
                    RandomUtils.uuid(),
                    Message.ReturnCode.OAUTH_PROVIDER_INVALID,
                    Message.Messages.OAUTH_PROVIDER_INVALID
            );
        }

        // 获取用户信息
        if (!(client instanceof @SuppressWarnings("rawtypes")Identifiable identifiable)) {
            throw new NikukyuException(
                    RandomUtils.uuid(),
                    Message.ReturnCode.OAUTH_PROVIDER_INVALID,
                    Message.Messages.OAUTH_PROVIDER_INVALID
            );
        }

        @SuppressWarnings("rawtypes") Callback identify = identifiable.identify(callback.token());
        if (identify == null) {
            throw new NikukyuException(
                    RandomUtils.uuid(),
                    Message.ReturnCode.OAUTH_PROVIDER_INVALID,
                    Message.Messages.OAUTH_PROVIDER_INVALID
            );
        }

        if (identify.throwable() != null) {
            throw new NikukyuException(
                    RandomUtils.uuid(),
                    Message.ReturnCode.OAUTH_PROVIDER_REQUEST_NETWORK_EXCEPTION,
                    Message.Messages.OAUTH_PROVIDER_REQUEST_NETWORK_EXCEPTION
            );
        }

        if (identify.data() == null && !identify.success() && identify.wrong() != null) {
            throw new NikukyuException(
                    RandomUtils.uuid(),
                    Message.ReturnCode.OAUTH_PROVIDER_REQUEST_EXCEPTION,
                    identify.wrong().toString()
            );
        }

        if (identify.data() == null) {
            throw new NikukyuException(
                    RandomUtils.uuid(),
                    Message.ReturnCode.OAUTH_PROVIDER_INVALID,
                    Message.Messages.OAUTH_PROVIDER_INVALID
            );
        }

        // 邮箱
        String oepnid = ((Identify) identify.data()).openid();
        String email = ((Identify) identify.data()).email();

        // 获取当前的 openid 是否已经绑定了账号
        Account account = accountService.getAccountWithEmail(email);
        AccountAuthorization authorization = accountService.getAccountAuthorization(oepnid);

        // OAuth 未被注册但邮箱已被占用
        if (account != null && authorization == null) {
            String uuid = RandomUtils.uuid();

            log.info("email: {} already exist", email);
            throw new NikukyuException(
                    uuid,
                    Message.ReturnCode.MAIL_EXIST,
                    Message.Messages.EMAIL_EXIST
            );
        }

        // 如果已经存入系统则直接返回 Token
        if (authorization != null) {
            // 如果存在则从 authorizations 中获取 auid
            account = accountService.getAccount(authorization.createdBy());

            // 创建 token
            Token token = tokenService.signature(
                    account.accountId(),
                    TimeUtils.getMilliUnixTime() + TokenService.Expire.WEEK,
                    Role.allPermissions()
            );

            return Map.of("token", token);
        }

        // 如果还没存在则创建一个新的 AccountAuthorization 然后返回一个仅有发送 email 权限的 token 要求用户验证
        authorization = new AccountAuthorization(
                RandomUtils.uuid(),
                TimeUtils.getMilliUnixTime(),
                null,
                provider,
                oepnid
        );

        // 创建 token
        Token token = tokenService.signature(
                null,
                TimeUtils.getMilliUnixTime() + TokenService.Expire.MINUTE * 10,
                Set.of(Permission.VERIFY_OAUTH_EMAIL)
        );

        @SuppressWarnings("unused") MailVerifyFlow flow =
                loginService.createMailVerifyFlow(token, email, authorization, (Identify) identify.data());

        return Map.of("token", token, "email", email == null ? "" : email);
    }

    @GetMapping("/login/verify/token")
    public Object token() {
        return tokenService.signature(
                null,
                TimeUtils.getMilliUnixTime() + TokenService.Expire.MINUTE * 5,
                Set.of(Permission.VERIFY_EMAIL)
        );
    }

    @SneakyThrows
    @SuppressWarnings("all")
    @PostMapping("/login/email/verify")
    @NikukyuPermissionCheck(
            access = {Permission.VERIFY_OAUTH_EMAIL, Permission.VERIFY_EMAIL},
            requiredAllAccess = false
    )
    public Object verifyEmail(@RequestParam("email") String email, @RequestHeader("Authorization") String token) {
        MailVerifyFlow flow = loginService.getVerifyFlow(tokenService.parse(token));

        // 检查参数
        if (flow == null) {
            flow = loginService.createMailVerifyFlow(tokenService.parse(token), email, null, null);
        }

        // 对照 email
        if (!Objects.equals(email, flow.email())) {
            loginService.createMailVerifyFlow(tokenService.parse(token), email, flow.accountAuthorization(), flow.identify());
        }

        if (!FormatUtils.checkEmail(email)) {
            String uuid = RandomUtils.uuid();

            log.info("email: {} format invalid", email);
            throw new NikukyuException(
                    uuid,
                    Message.ReturnCode.EMAIL_FORMAT_INVALID,
                    Message.Messages.EMAIL_FORMAT_INVALID
            );
        }

        // 发送邮件
        mailService.sendVerifyMail(flow.email(), flow.code());

        return Map.of("email", email);
    }

    @PostMapping("/login/token")
    @SuppressWarnings("all")
    @NikukyuPermissionCheck(
            access = {Permission.VERIFY_OAUTH_EMAIL, Permission.VERIFY_EMAIL},
            requiredAllAccess = false
    )
    public Object getToken(
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "code", required = false) String code,
            @RequestHeader("Authorization") String bearer
    ) {
        Token token = tokenService.parse(bearer);

        // 查询
        MailVerifyFlow flow = loginService.getVerifyFlow(token);

        // 验证不存在
        if (flow == null) {
            String uuid = RandomUtils.uuid();

            log.info("token invalid: {}", token.token());
            throw new NikukyuException(
                    uuid,
                    Message.ReturnCode.MAIL_VERIFY_INVALID,
                    Message.Messages.EMAIL_VERIFY_INVALID
            );
        }
        // 查询是否超时
        if (flow.expire() < TimeUtils.getMilliUnixTime()) {
            String uuid = RandomUtils.uuid();

            log.info("token expired: {}", token.token());
            throw new NikukyuException(
                    uuid,
                    Message.ReturnCode.MAIL_VERIFY_INVALID,
                    Message.Messages.EMAIL_VERIFY_INVALID
            );
        }

        // 验证码是否正确
        if (!flow.code().equals(code)) {
            String uuid = RandomUtils.uuid();

            log.info("token invalid: {}", token.token());
            throw new NikukyuException(
                    uuid,
                    Message.ReturnCode.MAIL_VERIFY_INVALID,
                    Message.Messages.EMAIL_VERIFY_INVALID
            );
        }

        // 信息
        Identify identify = flow.identify();

        // 验证成功
        // 创建 Account
        Account account = accountService.createAccount(
                true,
                RandomUtils.string(8),
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
                    account.accountId(),
                    temp.provider(),
                    temp.openid()
            );
        }

        // 签发 Token
        return tokenService.signature(
                account.accountId(),
                TimeUtils.getMilliUnixTime() + TokenService.Expire.WEEK,
                Role.allPermissions()
        );
    }
}
