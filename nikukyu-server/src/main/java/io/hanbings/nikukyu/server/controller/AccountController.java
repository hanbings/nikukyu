package io.hanbings.nikukyu.server.controller;

import io.hanbings.nikukyu.server.annotation.NikukyuPermissionCheck;
import io.hanbings.nikukyu.server.data.AccountDto;
import io.hanbings.nikukyu.server.exception.NotFoundException;
import io.hanbings.nikukyu.server.model.Account;
import io.hanbings.nikukyu.server.model.AccountAuthorization;
import io.hanbings.nikukyu.server.model.AccountLog;
import io.hanbings.nikukyu.server.model.AccountOAuth;
import io.hanbings.nikukyu.server.service.AccountService;
import io.hanbings.nikukyu.server.utils.RandomUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/account")
public class AccountController {
    final HttpServletRequest request;
    final AccountService accountService;

    @GetMapping("{account_id}")
    @NikukyuPermissionCheck(access = {"account:read"})
    public Object getAccount(@PathVariable("account_id") String accountId) {
        @SuppressWarnings("all")
        String tokenAccountId = request.getHeader("X-ACCOUNT-ID");
        if (!accountId.equals(tokenAccountId))
            throw new NotFoundException(RandomUtils.uuid(), accountId, request.getRequestURI());

        Account account = accountService.getAccount(accountId);
        if (account == null) throw new NotFoundException(RandomUtils.uuid(), accountId, request.getRequestURI());

        return account;
    }

    @PostMapping("{account_id}")
    @NikukyuPermissionCheck(access = {"account:update"})
    public Object updateAccount(@PathVariable("account_id") String accountId, @RequestBody AccountDto dto) {
        @SuppressWarnings("all")
        String tokenAccountId = request.getHeader("X-ACCOUNT-ID");
        if (!accountId.equals(tokenAccountId))
            throw new NotFoundException(RandomUtils.uuid(), accountId, request.getRequestURI());

        Account updated = accountService.updateAccount(
                tokenAccountId,
                dto.nickname(),
                dto.avatar(),
                dto.background(),
                dto.color()
        );
        if (updated == null) throw new NotFoundException(RandomUtils.uuid(), accountId, request.getRequestURI());

        return updated;
    }

    @GetMapping("{account_id}/authorization")
    @NikukyuPermissionCheck(access = {"account.authorization:read"})
    public Object getAuthorizationList(
            @PathVariable String account_id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String tokenAccountId = request.getHeader("X-ACCOUNT-ID");
        if (!account_id.equals(tokenAccountId))
            throw new NotFoundException(RandomUtils.uuid(), account_id, request.getRequestURI());

        return accountService.getAccountAuthorizationList(account_id, page, size);
    }

    @GetMapping("{account_id}/authorization/{authorization_id}")
    @NikukyuPermissionCheck(access = {"account.authorization:read"})
    public Object getAuthorization(
            @PathVariable("account_id") String accountId,
            @PathVariable("authorization_id") String authorizationId
    ) {
        String tokenAccountId = request.getHeader("X-ACCOUNT-ID");
        if (!accountId.equals(tokenAccountId))
            throw new NotFoundException(RandomUtils.uuid(), authorizationId, request.getRequestURI());

        AccountAuthorization accountAuthorization = accountService.getAccountAuthorization(accountId, authorizationId);
        if (accountAuthorization == null)
            throw new NotFoundException(RandomUtils.uuid(), authorizationId, request.getRequestURI());

        return accountAuthorization;
    }

    @DeleteMapping("{account_id}/authorization/{authorization_id}")
    @NikukyuPermissionCheck(access = {"account.authorization:delete"})
    public Object deleteAuthorization(
            @PathVariable("account_id") String accountId,
            @PathVariable("authorization_id") String authorizationId
    ) {
        String tokenAccountId = request.getHeader("X-ACCOUNT-ID");
        if (!accountId.equals(tokenAccountId))
            throw new NotFoundException(RandomUtils.uuid(), authorizationId, request.getRequestURI());

        return accountService.deleteAccountAuthorization(accountId, authorizationId);
    }

    @GetMapping("{account_id}/log")
    @NikukyuPermissionCheck(access = {"account.log:read"})
    public Object getLogList(
            @PathVariable String account_id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String tokenAccountId = request.getHeader("X-ACCOUNT-ID");
        if (!account_id.equals(tokenAccountId))
            throw new NotFoundException(RandomUtils.uuid(), account_id, request.getRequestURI());

        return accountService.getAccountLogList(account_id, page, size);
    }

    @GetMapping("{account_id}/log/{log_id}")
    @NikukyuPermissionCheck(access = {"account.log:read"})
    public Object getLog(
            @PathVariable("account_id") String accountId,
            @PathVariable("log_id") String logId
    ) {
        String tokenAccountId = request.getHeader("X-ACCOUNT-ID");
        if (!accountId.equals(tokenAccountId))
            throw new NotFoundException(RandomUtils.uuid(), logId, request.getRequestURI());

        AccountLog accountLog = accountService.getAccountLog(accountId, logId);
        if (accountLog == null) throw new NotFoundException(RandomUtils.uuid(), logId, request.getRequestURI());

        return accountLog;
    }

    @GetMapping("{account_id}/oauth")
    @NikukyuPermissionCheck(access = {"account.oauth:read"})
    public Object getOAuthList(
            @PathVariable("account_id") String accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String tokenAccountId = request.getHeader("X-ACCOUNT-ID");
        if (!accountId.equals(tokenAccountId))
            throw new NotFoundException(RandomUtils.uuid(), accountId, request.getRequestURI());

        return accountService.getAccountOAuthList(accountId, page, size);
    }

    @GetMapping("{account_id}/oauth/{oauth_id}")
    @NikukyuPermissionCheck(access = {"account.oauth:read"})
    public Object getOAuth(
            @PathVariable("account_id") String accountId,
            @PathVariable("oauth_id") String oauthId
    ) {
        String tokenAccountId = request.getHeader("X-ACCOUNT-ID");
        if (!accountId.equals(tokenAccountId))
            throw new NotFoundException(RandomUtils.uuid(), oauthId, request.getRequestURI());

        AccountOAuth accountOAuth = accountService.getAccountOAuth(accountId, oauthId);
        if (accountOAuth == null) throw new NotFoundException(RandomUtils.uuid(), oauthId, request.getRequestURI());

        return accountOAuth;
    }

    @DeleteMapping("{account_id}/oauth/{oauth_id}")
    @NikukyuPermissionCheck(access = {"account.oauth:delete"})
    public Object deleteOAuth(
            @PathVariable("account_id") String accountId,
            @PathVariable("oauth_id") String oauthId
    ) {
        String tokenAccountId = request.getHeader("X-ACCOUNT-ID");
        if (!accountId.equals(tokenAccountId))
            throw new NotFoundException(RandomUtils.uuid(), oauthId, request.getRequestURI());

        return accountService.deleteAccountOAuth(accountId, oauthId);
    }
}
