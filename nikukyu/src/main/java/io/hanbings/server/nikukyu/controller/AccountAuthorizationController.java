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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0")
public class AccountAuthorizationController {
    final TokenService tokens;
    final AccountService accounts;

    @GetMapping("/account/authorization")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_AUTHORIZATION_READ})
    public Message<?> authorizations(@RequestHeader("Authorization") String token) {
        Token t = tokens.get(token);

        return Message.success(accounts.getAccountAuthorizationsWithAuid(t.belong()));
    }
}
