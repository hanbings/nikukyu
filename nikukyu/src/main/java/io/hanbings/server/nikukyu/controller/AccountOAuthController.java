package io.hanbings.server.nikukyu.controller;

import io.hanbings.server.nikukyu.annotation.NikukyuTokenCheck;
import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.data.Message;
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
public class AccountOAuthController {
    final TokenService tokens;
    final AccountService accounts;

    @GetMapping("/account/{auid}/oauth")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_OAUTH_READ})
    public Message<?> list(
            @PathVariable String auid,
            @RequestHeader("Authorization") String token
    ) {
        Token t = tokens.get(token.substring(7));
        if (Objects.equals(t.belong().toString(), auid)) return Message.unauthorized(null);

        return null;
    }
}
