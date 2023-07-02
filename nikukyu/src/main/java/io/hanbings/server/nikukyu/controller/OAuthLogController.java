package io.hanbings.server.nikukyu.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.hanbings.server.nikukyu.annotation.NikukyuTokenCheck;
import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.data.Message;
import io.hanbings.server.nikukyu.data.Permission;
import io.hanbings.server.nikukyu.data.Token;
import io.hanbings.server.nikukyu.service.OAuthService;
import io.hanbings.server.nikukyu.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0")
@SuppressWarnings("SpellCheckingInspection")
public class OAuthLogController {
    final TokenService tokens;
    final OAuthService oauths;

    @GetMapping("/oauth/{ouid}/log")
    @SaCheckPermission(Permission.ACCOUNT_LOG_READ)
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_LOG_READ})
    public Message<?> list(
            @PathVariable String ouid,
            @RequestHeader("Authorization") String token

    ) {
        Token t = tokens.get(token.substring(7));

        return null;
    }

    @GetMapping("/oauth/{ouid}/log/{olid}")
    @SaCheckPermission(Permission.ACCOUNT_LOG_READ)
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_LOG_READ})
    public Message<?> read(
            @PathVariable String ouid,
            @PathVariable String olid,
            @RequestHeader("Authorization") String token

    ) {
        Token t = tokens.get(token.substring(7));

        return null;
    }
}
