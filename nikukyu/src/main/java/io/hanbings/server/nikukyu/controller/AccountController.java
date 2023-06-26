package io.hanbings.server.nikukyu.controller;

import io.hanbings.server.nikukyu.annotation.NikukyuTokenCheck;
import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.data.Account;
import io.hanbings.server.nikukyu.model.Message;
import io.hanbings.server.nikukyu.model.Token;
import io.hanbings.server.nikukyu.service.AccountService;
import io.hanbings.server.nikukyu.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0")
@SuppressWarnings("SpellCheckingInspection")
public class AccountController {
    final TokenService tokens;
    final AccountService accounts;

    @GetMapping("/account")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_READ})
    public Message<?> account(@RequestHeader("Authorization") String token) {
        Token t = tokens.get(token.substring(7));
        Account account = accounts.getAccountWithAuid(t.belong());

        return Message.success(account);
    }


    @PostMapping("/account/{auid}")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_WRITE})
    public Message<?> account(
            @PathVariable String auid,
            @RequestHeader("Authorization") String token
    ) {
        Token t = tokens.get(token.substring(7));
        Account account = accounts.getAccountWithAuid(t.belong());

        return Message.success(account);
    }

    @GetMapping("/account/{auid}/settings")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_SETTINGS_READ})
    public Message<?> readSetting(
            @PathVariable String auid,
            @RequestHeader("Authorization") String token
    ) {
        Token t = tokens.get(token.substring(7));
        Account account = accounts.getAccountWithAuid(t.belong());

        return Message.success(account);
    }

    @PostMapping("/account/{auid}/settings")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_SETTINGS_WRITE})
    public Message<?> changeSetting(
            @PathVariable String auid,
            @RequestHeader("Authorization") String token
    ) {
        Token t = tokens.get(token.substring(7));
        Account account = accounts.getAccountWithAuid(t.belong());

        return Message.success(account);
    }
}
