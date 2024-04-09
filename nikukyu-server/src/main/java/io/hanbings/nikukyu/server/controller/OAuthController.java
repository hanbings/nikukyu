package io.hanbings.nikukyu.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/oauth")
public class OAuthController {
    @GetMapping()
    public Object getOAuthList() {
        return null;
    }

    @PostMapping
    public Object createOAuth() {
        return null;
    }

    @GetMapping("{oauth_id}")
    public Object getOAuth(@PathVariable("oauth_id") String oauthId) {
        return null;
    }

    @PostMapping("{oauth_id}")
    public Object updateOAuth(@PathVariable("oauth_id") String oauthId) {
        return null;
    }

    @DeleteMapping("{oauth_id}")
    public Object deleteOAuth(@PathVariable("oauth_id") String oauthId) {
        return null;
    }

    @GetMapping("{oauth_id}/client")
    public Object getClientList(@PathVariable("oauth_id") String oauthId) {
        return null;
    }

    @PostMapping("{oauth_id}/client")
    public Object createClient(@PathVariable("oauth_id") String oauthId) {
        return null;
    }

    @GetMapping("{oauth_id}/client/{client_id}")
    public Object getClient(
            @PathVariable("oauth_id") String oauthId,
            @PathVariable("client_id") String clientId
    ) {
        return null;
    }

    @PostMapping("{oauth_id}/client/{client_id}")
    public Object updateClient(
            @PathVariable("oauth_id") String oauthId,
            @PathVariable("client_id") String clientId
    ) {
        return null;
    }

    @DeleteMapping("{oauth_id}/client/{client_id}")
    public Object deleteClient(
            @PathVariable("oauth_id") String oauthId,
            @PathVariable("client_id") String clientId
    ) {
        return null;
    }

    @GetMapping("{oauth_id}/log")
    public Object getLogList(@PathVariable("oauth_id") String oauthId) {
        return null;
    }

    @GetMapping("{oauth_id}/log/{log_id}")
    public Object getLog(
            @PathVariable("oauth_id") String oauthId,
            @PathVariable("log_id") String logId
    ) {
        return null;
    }
}
