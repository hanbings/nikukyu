package io.hanbings.nikukyu.server.controller;

import cn.dev33.satoken.stp.SaLoginConfig;
import cn.dev33.satoken.stp.StpUtil;
import io.hanbings.flows.common.OAuth;
import io.hanbings.flows.common.interfaces.Access;
import io.hanbings.flows.common.interfaces.Callback;
import io.hanbings.flows.common.interfaces.Identifiable;
import io.hanbings.flows.common.interfaces.Identify;
import io.hanbings.nikukyu.server.config.Config;
import io.hanbings.nikukyu.server.data.MailVerifyFlow;
import io.hanbings.nikukyu.server.data.Message;
import io.hanbings.nikukyu.server.exception.NikukyuException;
import io.hanbings.nikukyu.server.model.Account;
import io.hanbings.nikukyu.server.model.AccountAuthorization;
import io.hanbings.nikukyu.server.security.PermissionChecker;
import io.hanbings.nikukyu.server.service.AccountService;
import io.hanbings.nikukyu.server.service.LoginService;
import io.hanbings.nikukyu.server.service.MailService;
import io.hanbings.nikukyu.server.utils.FormatUtils;
import io.hanbings.nikukyu.server.utils.RandomUtils;
import io.hanbings.nikukyu.server.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0")
public class LoginController {
    final Config config;
    final MailService mailService;
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
                    Message.ReturnCode.OAUTH_PROVIDER_INVALID,
                    Message.Messages.OAUTH_PROVIDER_INVALID
            );
        }

        if (callback.throwable() != null) {
            throw new NikukyuException(
                    Message.ReturnCode.OAUTH_PROVIDER_REQUEST_NETWORK_EXCEPTION,
                    Message.Messages.OAUTH_PROVIDER_REQUEST_NETWORK_EXCEPTION
            );
        }

        if (callback.data() == null && !callback.success() && callback.wrong() != null) {
            throw new NikukyuException(
                    Message.ReturnCode.OAUTH_PROVIDER_REQUEST_EXCEPTION,
                    ((Access.Wrong) callback.wrong()).error()
            );
        }

        if (callback.data() == null) {
            throw new NikukyuException(
                    Message.ReturnCode.OAUTH_PROVIDER_INVALID,
                    Message.Messages.OAUTH_PROVIDER_INVALID
            );
        }

        // 获取用户信息
        if (!(client instanceof @SuppressWarnings("rawtypes")Identifiable identifiable)) {
            throw new NikukyuException(
                    Message.ReturnCode.OAUTH_PROVIDER_INVALID,
                    Message.Messages.OAUTH_PROVIDER_INVALID
            );
        }

        @SuppressWarnings("rawtypes") Callback identify = identifiable.identify(callback.token());
        if (identify == null) {
            throw new NikukyuException(
                    Message.ReturnCode.OAUTH_PROVIDER_INVALID,
                    Message.Messages.OAUTH_PROVIDER_INVALID
            );
        }

        if (identify.throwable() != null) {
            throw new NikukyuException(
                    Message.ReturnCode.OAUTH_PROVIDER_REQUEST_NETWORK_EXCEPTION,
                    Message.Messages.OAUTH_PROVIDER_REQUEST_NETWORK_EXCEPTION
            );
        }

        if (identify.data() == null && !identify.success() && identify.wrong() != null) {
            throw new NikukyuException(
                    Message.ReturnCode.OAUTH_PROVIDER_REQUEST_EXCEPTION,
                    identify.wrong().toString()
            );
        }

        if (identify.data() == null) {
            throw new NikukyuException(
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
                    Message.ReturnCode.MAIL_EXIST,
                    Message.Messages.EMAIL_EXIST
            );
        }

        // 如果已经存入系统则直接返回 Token
        if (authorization != null) {
            // 如果存在则从 authorizations 中获取 auid
            account = accountService.getAccount(authorization.getCreatedBy());

            // 创建 token
            StpUtil.login(account.getId(), SaLoginConfig.setTimeout(600).setActiveTimeout(600));
            String token = StpUtil.getTokenValue();

            return Map.of("token", token);
        }

        // 如果还没存在则创建一个新的 AccountAuthorization 然后返回一个仅有发送 email 权限的 token 要求用户验证
        authorization = new AccountAuthorization(
                null,
                null,
                TimeUtils.getMilliUnixTime(),
                null,
                provider,
                oepnid
        );

        // 创建 token
        StpUtil.login(email, SaLoginConfig.setTimeout(600).setActiveTimeout(600));
        String token = StpUtil.getTokenValue();
        PermissionChecker.temporaryPermissions.put(token, List.of("email:verify"));

        @SuppressWarnings("unused") MailVerifyFlow flow =
                loginService.createMailVerifyFlow(token, email, authorization, (Identify) identify.data());

        return Map.of("token", token, "email", email == null ? "" : email);
    }

    @SneakyThrows
    @SuppressWarnings("all")
    @PostMapping("/login/email/verify")
    public Object verifyEmail(@RequestParam("email") String email) {
        StpUtil.checkPermission("email:verify");
        String token = StpUtil.getTokenValue();

        MailVerifyFlow flow = loginService.getVerifyFlow(token);

        // 检查参数
        if (flow == null) {
            flow = loginService.createMailVerifyFlow(token, email, null, null);
        }

        // 对照 email
        if (!Objects.equals(email, flow.email())) {
            loginService.createMailVerifyFlow(token, email, flow.accountAuthorization(), flow.identify());
        }

        if (!FormatUtils.checkEmail(email)) {
            String uuid = RandomUtils.uuid();

            log.info("email: {} format invalid", email);
            throw new NikukyuException(
                    Message.ReturnCode.EMAIL_FORMAT_INVALID,
                    Message.Messages.EMAIL_FORMAT_INVALID
            );
        }

        // 发送邮件
        mailService.sendVerifyMail(flow.email(), flow.code());

        return Map.of("email", email);
    }


    @SuppressWarnings("all")
    @PostMapping("/login/token")
    public Object getToken(
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "code", required = false) String code
    ) {
        StpUtil.checkPermission("email:verify");
        String token = StpUtil.getTokenValue();

        // 查询
        MailVerifyFlow flow = loginService.getVerifyFlow(token);

        // 验证不存在
        if (flow == null) {
            String uuid = RandomUtils.uuid();

            log.info("token invalid: {}", token);
            throw new NikukyuException(
                    Message.ReturnCode.MAIL_VERIFY_INVALID,
                    Message.Messages.EMAIL_VERIFY_INVALID
            );
        }
        // 查询是否超时
        if (flow.expire() < TimeUtils.getMilliUnixTime()) {
            String uuid = RandomUtils.uuid();

            log.info("token expired: {}", token);
            throw new NikukyuException(
                    Message.ReturnCode.MAIL_VERIFY_INVALID,
                    Message.Messages.EMAIL_VERIFY_INVALID
            );
        }

        // 验证码是否正确
        if (!flow.code().equals(code)) {
            String uuid = RandomUtils.uuid();

            log.info("token invalid: {}", token);
            throw new NikukyuException(
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
                    account.getId(),
                    temp.getProvider(),
                    temp.getOpenid()
            );
        }

        StpUtil.login(account.getId());
        String tokened = StpUtil.getTokenValue();

        // 签发 Token
        return Map.of("token", tokened);
    }
}
