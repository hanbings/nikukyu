package io.hanbings.server.nikukyu.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.hanbings.server.nikukyu.annotation.NikukyuTokenCheck;
import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.data.Message;
import io.hanbings.server.nikukyu.data.Permission;
import io.hanbings.server.nikukyu.data.Token;
import io.hanbings.server.nikukyu.service.AccountService;
import io.hanbings.server.nikukyu.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0")
@SuppressWarnings("SpellCheckingInspection")
public class AccountAuthorizationController {
    final TokenService tokens;
    final AccountService accounts;

    @GetMapping("/account/{auid}/authorization")
    @SaCheckPermission(Permission.ACCOUNT_AUTHORIZATION_READ)
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_AUTHORIZATION_READ})
    public Message<?> authorizations(
            @PathVariable String auid,
            @RequestHeader("Authorization") String token
    ) {
        Token t = tokens.get(token.substring(7));
        if (Objects.equals(t.belong().toString(), auid)) return Message.unauthorized(null);

        return Message.success(accounts.getAccountAuthorizationsWithAuid(t.belong()));
    }
}
