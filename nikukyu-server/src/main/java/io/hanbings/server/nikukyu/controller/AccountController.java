package io.hanbings.server.nikukyu.controller;

import io.hanbings.server.nikukyu.annotation.NikukyuTokenCheck;
import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.data.Message;
import io.hanbings.server.nikukyu.data.Token;
import io.hanbings.server.nikukyu.exception.NotFoundException;
import io.hanbings.server.nikukyu.exception.UnauthorizedException;
import io.hanbings.server.nikukyu.model.Account;
import io.hanbings.server.nikukyu.model.AccountAuthorization;
import io.hanbings.server.nikukyu.model.AccountLog;
import io.hanbings.server.nikukyu.model.AccountOAuth;
import io.hanbings.server.nikukyu.service.AccountService;
import io.hanbings.server.nikukyu.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

        if (!token.belong().equals(auid)) throw new UnauthorizedException();

        Account account = accountService.getAccountWithAuid(token.belong());

        return Message.success(account);
    }

    @PostMapping("/account/{auid}")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_WRITE}, checkAccount = true)
    public Message<?> updateAccount(
            @PathVariable String auid,
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "nick", required = false) String nick,
            @RequestParam(value = "avatar", required = false) String avatar,
            @RequestParam(value = "background", required = false) String background,
            @RequestParam(value = "color", required = false) String color
    ) {
        Account account = accountService.updateAccount(auid, id, nick, avatar, background, color);

        return Message.success(account);
    }

    // authorization
    @GetMapping("/account/{auid}/authorization")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_AUTHORIZATION_READ}, checkAccount = true)
    public Message<?> getAuthorization(@PathVariable String auid) {
        List<AccountAuthorization> authorizations = accountService.getAccountAuthorizationsWithAuid(auid);

        return Message.success(authorizations);
    }

    @GetMapping("/account/{auid}/authorization/{aaid}")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_AUTHORIZATION_READ}, checkAccount = true)
    public Message<?> getAuthorization(@PathVariable String auid, @PathVariable String aaid) {
        AccountAuthorization authorization = accountService.getAccountAuthorizationWithAaid(aaid);

        if (authorization != null && authorization.auid().equals(auid)) {
            return Message.success(authorization);
        }

        throw new NotFoundException();
    }

    // log
    @GetMapping("/account/{auid}/log")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_LOG_READ}, checkAccount = true)
    public Message<?> getLog(@PathVariable String auid) {
        List<AccountLog> logs = accountService.getAccountLogsWithAuid(auid);

        return Message.success(logs);
    }

    @GetMapping("/account/{auid}/log/{alid}")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_LOG_READ}, checkAccount = true)
    public Message<?> getLog(@PathVariable String auid, @PathVariable String alid) {
        AccountLog log = accountService.getAccountLogWithAlid(alid);

        if (log != null && log.auid().equals(auid)) {
            return Message.success(log);
        }

        throw new NotFoundException();
    }

    // oauth
    @GetMapping("/account/{auid}/oauth")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_OAUTH_READ}, checkAccount = true)
    public Message<?> getOAuth(@PathVariable String auid) {
        List<AccountOAuth> oAuth = accountService.getAccountOAuthsWithAuid(auid);

        return Message.success(oAuth);
    }

    @GetMapping("/account/{auid}/oauth/{aoid}")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_OAUTH_READ}, checkAccount = true)
    public Message<?> getOAuth(@PathVariable String auid, @PathVariable String aoid) {
        AccountOAuth oAuth = accountService.getAccountOAuthWithAoid(aoid);

        if (oAuth != null && oAuth.auid().equals(auid)) {
            return Message.success(oAuth);
        }

        throw new NotFoundException();
    }

    @DeleteMapping("/account/{auid}/oauth/{aoid}")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_OAUTH_WRITE}, checkAccount = true)
    public Message<?> deleteOAuth(@PathVariable @SuppressWarnings("unused") String auid, @PathVariable String aoid) {
        accountService.deleteAccountOAuthWithAoid(aoid);

        return Message.success(null);
    }
}
