package io.hanbings.server.nikukyu.controller;

import io.hanbings.server.nikukyu.annotation.NikukyuTokenCheck;
import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.model.Message;
import io.hanbings.server.nikukyu.model.Token;
import io.hanbings.server.nikukyu.service.AccountService;
import io.hanbings.server.nikukyu.service.OAuthService;
import io.hanbings.server.nikukyu.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0")
@SuppressWarnings("SpellCheckingInspection")
public class OAuthController {
    final TokenService tokens;
    final AccountService accounts;
    final OAuthService oauths;

    @GetMapping("/oauth/authorize")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_AUTHORIZE})
    public Message<?> authorize(@RequestHeader("Authorization") String token) {
        Token t = tokens.get(token);

        return Message.success(oauths.getOAuthWithAuid(t.belong()));
    }
}
