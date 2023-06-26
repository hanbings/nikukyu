package io.hanbings.server.nikukyu.controller;

import io.hanbings.server.nikukyu.annotation.NikukyuTokenCheck;
import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.data.OAuth;
import io.hanbings.server.nikukyu.model.Message;
import io.hanbings.server.nikukyu.model.Token;
import io.hanbings.server.nikukyu.service.AccountService;
import io.hanbings.server.nikukyu.service.OAuthService;
import io.hanbings.server.nikukyu.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0")
@SuppressWarnings("SpellCheckingInspection")
public class OAuthClientController {
    final TokenService tokens;
    final AccountService accounts;
    final OAuthService oauths;

    @GetMapping("/oauth")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_CLIENT_READ})
    public Message<?> info(@RequestHeader("Authorization") String token) {
        Token t = tokens.get(token.substring(7));
        List<OAuth> oauth = oauths.getOAuthWithAuid(t.belong());

        return Message.success(oauth);
    }

    @PostMapping("/oauth")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_CLIENT_WRITE})
    public Message<?> oauth(@RequestHeader("Authorization") String token) {
        Token t = tokens.get(token.substring(7));
        List<OAuth> oauth = oauths.getOAuthWithAuid(t.belong());

        return Message.success(oauth);
    }

    @PostMapping("/oauth/{ouid}")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_CLIENT_WRITE})
    public Message<?> oauth(
            @PathVariable String ouid,
            @RequestHeader("Authorization") String token
    ) {
        Token t = tokens.get(token.substring(7));
        List<OAuth> oauth = oauths.getOAuthWithAuid(t.belong());

        return Message.success(oauth);
    }

    @PostMapping("/oauth/token")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_TOKEN})
    public Message<?> token(@RequestHeader("Authorization") String token) {
        Token t = tokens.get(token.substring(7));
        List<OAuth> oauth = oauths.getOAuthWithAuid(t.belong());

        return Message.success(oauth);
    }
}
