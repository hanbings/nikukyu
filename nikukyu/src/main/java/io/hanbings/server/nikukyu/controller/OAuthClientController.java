package io.hanbings.server.nikukyu.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.hanbings.server.nikukyu.annotation.NikukyuTokenCheck;
import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.data.Message;
import io.hanbings.server.nikukyu.data.Permission;
import io.hanbings.server.nikukyu.data.Token;
import io.hanbings.server.nikukyu.model.OAuth;
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
    @SaCheckPermission(Permission.OAUTH_CLIENT_READ)
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
    @SaCheckPermission(Permission.OAUTH_CLIENT_WRITE)
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
    @SaCheckPermission(Permission.OAUTH_CLIENT_READ)
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
    @SaCheckPermission(Permission.OAUTH_CLIENT_WRITE)
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
