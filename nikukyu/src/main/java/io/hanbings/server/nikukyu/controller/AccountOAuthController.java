package io.hanbings.server.nikukyu.controller;

import io.hanbings.server.nikukyu.annotation.NikukyuTokenCheck;
import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.model.Message;
import io.hanbings.server.nikukyu.model.Token;
import io.hanbings.server.nikukyu.service.AccountService;
import io.hanbings.server.nikukyu.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@RequiredArgsConstructor
public class AccountOAuthController {
    final TokenService tokens;
    final AccountService accounts;

    @GetMapping("/account/oauth")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_OAUTH_READ})
    public Message<?> readOAuth(@RequestHeader("Authorization") String token) {
        Token t = tokens.get(token.substring(7));

        return null;
    }
}
