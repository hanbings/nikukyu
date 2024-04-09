package io.hanbings.nikukyu.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/account")
public class AccountController {

    @GetMapping()
    public Object getAccountList() {
        return null;
    }

    @GetMapping("{account_id}")
    public Object getAccount(@PathVariable("account_id") String accountId) {
        return null;
    }

    @PostMapping("{account_id}")
    public Object updateAccount(@PathVariable("account_id") String accountId) {
        return null;
    }

    @GetMapping("{account_id}/authorization")
    public Object getAuthorizationList(@PathVariable String account_id) {
        return null;
    }

    @GetMapping("{account_id}/authorization/{authorization_id}")
    public Object getAuthorization(
            @PathVariable("account_id") String accountId,
            @PathVariable("authorization_id") String authorizationId
    ) {
        return null;
    }

    @DeleteMapping("{account_id}/authorization/{authorization_id}")
    public Object deleteAuthorization(
            @PathVariable("account_id") String accountId,
            @PathVariable("authorization_id") String authorizationId
    ) {
        return null;
    }

    @GetMapping("{account_id}/log")
    public Object getLogList(@PathVariable String account_id) {
        return null;
    }

    @GetMapping("{account_id}/log/{log_id}")
    public Object getLog(
            @PathVariable("account_id") String accountId,
            @PathVariable("log_id") String logId
    ) {
        return null;
    }

    @GetMapping("{account_id}/oauth")
    public Object getOAuthList(@PathVariable("account_id") String accountId) {
        return null;
    }

    @GetMapping("{account_id}/oauth/{oauth_id}")
    public Object getOAuth(
            @PathVariable("account_id") String accountId,
            @PathVariable("oauth_id") String oauthId
    ) {
        return null;
    }

    @PostMapping("{account_id}/oauth/{oauth_id}")
    public Object updateOAuth(
            @PathVariable("account_id") String accountId,
            @PathVariable("oauth_id") String oauthId
    ) {
        return null;
    }

    @DeleteMapping("{account_id}/oauth/{oauth_id}")
    public Object deleteOAuth(
            @PathVariable("account_id") String accountId,
            @PathVariable("oauth_id") String oauthId
    ) {
        return null;
    }
}
