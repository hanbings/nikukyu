package io.hanbings.server.nikukyu.controller;

import io.hanbings.server.nikukyu.annotation.NikukyuTokenCheck;
import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.data.OAuth;
import io.hanbings.server.nikukyu.data.OAuthLog;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0")
@SuppressWarnings("SpellCheckingInspection")
public class OAuthLogController {
    final TokenService tokens;
    final AccountService accounts;
    final OAuthService oauths;

    @GetMapping("/oauth/log")
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_LOG_READ})
    public Message<?> logs(@RequestHeader("Authorization") String token) {
        Token t = tokens.get(token);
        List<OAuth> oauth = oauths.getOAuthWithAuid(t.belong());
        List<OAuthLog> log = oauth.stream()
                .map(OAuth::ouid)
                .map(oauths::getOAuthLogsWithOuid)
                .toList()
                .stream()
                .flatMap(List::stream)
                .toList();

        return Message.success(log);
    }
}
