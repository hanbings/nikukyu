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


    @GetMapping("/oauth/{ouid}/client")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_CLIENT_READ})
    public Message<?> list(
            @PathVariable String ouid,
            @RequestHeader("Authorization") String token
    ) {
        Token t = tokens.get(token.substring(7));
        List<OAuth> oauth = oauths.getOAuthWithAuid(t.belong());

        return Message.success(oauth);
    }

    @PostMapping("/oauth/{ouid}/client")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_CLIENT_WRITE})
    public Message<?> create(
            @PathVariable String ouid,
            @RequestHeader("Authorization") String token
    ) {
        Token t = tokens.get(token.substring(7));
        List<OAuth> oauth = oauths.getOAuthWithAuid(t.belong());

        return Message.success(oauth);
    }

    @GetMapping("/oauth/{ouid}/client/{ocid}")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_CLIENT_READ})
    public Message<?> read(
            @PathVariable String ouid,
            @PathVariable String ocid,
            @RequestHeader("Authorization") String token
    ) {
        Token t = tokens.get(token.substring(7));
        List<OAuth> oauth = oauths.getOAuthWithAuid(t.belong());

        return Message.success(oauth);
    }

    @PutMapping("/oauth/{ouid}/client/{ocid}")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_CLIENT_WRITE})
    public Message<?> change(
            @PathVariable String ouid,
            @PathVariable String ocid,
            @RequestHeader("Authorization") String token
    ) {
        Token t = tokens.get(token.substring(7));
        List<OAuth> oauth = oauths.getOAuthWithAuid(t.belong());

        return Message.success(oauth);
    }
}
