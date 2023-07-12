package io.hanbings.server.nikukyu.controller;

import io.hanbings.server.nikukyu.annotation.NikukyuTokenCheck;
import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.data.Message;
import io.hanbings.server.nikukyu.data.Token;
import io.hanbings.server.nikukyu.exception.ControllerException;
import io.hanbings.server.nikukyu.model.Account;
import io.hanbings.server.nikukyu.service.AccountService;
import io.hanbings.server.nikukyu.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0")
@SuppressWarnings("SpellCheckingInspection")
public class AccountController {
    final TokenService tokenService;
    final AccountService accountService;

    // account
    @GetMapping("/account")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_READ})
    public Message<?> getAccount(@RequestHeader("Authorization") String bearer) {
        Token token = tokenService.parse(bearer);
        Account account = accountService.getAccountWithAuid(token.belong());

        return Message.success(account);
    }

    @GetMapping("/account/{auid}")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_READ}, checkAccount = true)
    public Message<?> getAccount(
            @RequestHeader("Authorization") String bearer,
            @PathVariable String auid
    ) {
        Token token = tokenService.parse(bearer);

         if (!token.belong().toString().equals(auid)) throw new ControllerException(Message.Messages.UNAUTHORIZED);

        Account account = accountService.getAccountWithAuid(token.belong());

        return Message.success(account);
    }

    @PostMapping("/account/{auid}")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_WRITE})
    public Message<?> updateAccount(@PathVariable String auid) {
        return null;
    }

    // authorization
    @GetMapping("/account/{auid}/authorization")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_AUTHORIZATION_READ}, checkAccount = true)
    public Message<?> getAuthorization(@PathVariable String auid) {
        return null;
    }

    @GetMapping("/account/{auid}/authorization/{aaid}")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_AUTHORIZATION_READ}, checkAccount = true)
    public Message<?> getAuthorization(@PathVariable String auid, @PathVariable String aaid) {
        return null;
    }

    // log
    @GetMapping("/account/{auid}/log")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_LOG_READ}, checkAccount = true)
    public Message<?> getLog(@PathVariable String auid) {
        return null;
    }

    @GetMapping("/account/{auid}/log/{alid}")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_LOG_READ}, checkAccount = true)
    public Message<?> getLog(@PathVariable String auid, @PathVariable String alid) {
        return null;
    }

    // oauth
    @GetMapping("/account/{auid}/oauth")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_OAUTH_READ}, checkAccount = true)
    public Message<?> getOAuth(@PathVariable String auid) {
        return null;
    }

    @GetMapping("/account/{auid}/oauth/{ouid}")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_OAUTH_READ}, checkAccount = true)
    public Message<?> getOAuth(@PathVariable String auid, @PathVariable String ouid) {
        return null;
    }

    @DeleteMapping("/account/{auid}/oauth/{ouid}")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_OAUTH_WRITE}, checkAccount = true)
    public Message<?> deleteOAuth(@PathVariable String auid, @PathVariable String ouid) {
        return null;
    }
}
