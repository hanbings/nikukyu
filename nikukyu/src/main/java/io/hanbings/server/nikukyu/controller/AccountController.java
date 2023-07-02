package io.hanbings.server.nikukyu.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.hanbings.server.nikukyu.annotation.NikukyuTokenCheck;
import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.data.Message;
import io.hanbings.server.nikukyu.data.Permission;
import io.hanbings.server.nikukyu.data.Token;
import io.hanbings.server.nikukyu.model.Account;
import io.hanbings.server.nikukyu.service.AccountService;
import io.hanbings.server.nikukyu.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0")
@SuppressWarnings("SpellCheckingInspection")
public class AccountController {
    final TokenService tokens;
    final AccountService accounts;

    @GetMapping("/account")
    @SaCheckPermission(Permission.ACCOUNT_READ)
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_READ})
    public Message<?> info(@RequestHeader("Authorization") String token) {
        Token t = tokens.get(token.substring(7));
        Account account = accounts.getAccountWithAuid(t.belong());

        return Message.success(account);
    }

    @GetMapping("/account/{auid}")
    @SaCheckPermission(Permission.ACCOUNT_READ)
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_READ})
    public Message<?> read(
            @PathVariable String auid,
            @RequestHeader("Authorization") String token
    ) {
        Token t = tokens.get(token.substring(7));
        if (Objects.equals(t.belong().toString(), auid)) return Message.unauthorized(null);

        Account account = accounts.getAccountWithAuid(t.belong());

        return Message.success(account);
    }

    @PutMapping("/account/{auid}")
    @SaCheckPermission(Permission.ACCOUNT_WRITE)
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_WRITE})
    public Message<?> change(
            @PathVariable String auid,
            @RequestHeader("Authorization") String token
    ) {
        Token t = tokens.get(token.substring(7));
        if (Objects.equals(t.belong().toString(), auid)) return Message.unauthorized(null);

        Account account = accounts.getAccountWithAuid(t.belong());

        return Message.success(account);
    }
}
