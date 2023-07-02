package io.hanbings.server.nikukyu.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.hanbings.server.nikukyu.annotation.NikukyuTokenCheck;
import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.data.Message;
import io.hanbings.server.nikukyu.data.Permission;
import io.hanbings.server.nikukyu.data.Token;
import io.hanbings.server.nikukyu.model.AccountLog;
import io.hanbings.server.nikukyu.service.AccountService;
import io.hanbings.server.nikukyu.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0")
@SuppressWarnings("SpellCheckingInspection")
public class AccountLogController {
    final TokenService tokens;
    final AccountService accounts;

    @GetMapping("/account/{auid}/log")
    @SaCheckPermission(Permission.ACCOUNT_LOG_READ)
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_LOG_READ})
    public Message<?> list(
            @PathVariable String auid,
            @RequestHeader("Authorization") String token
    ) {
        Token t = tokens.get(token.substring(7));
        if (Objects.equals(t.belong().toString(), auid)) return Message.unauthorized(null);

        List<AccountLog> log = accounts.getAccountLogsWithAuid(t.belong());

        return Message.success(log);
    }

    @GetMapping("/account/{auid}/log/{alid}")
    @SaCheckPermission(Permission.ACCOUNT_LOG_READ)
    @NikukyuTokenCheck(access = {AccessType.ACCOUNT_LOG_READ})
    public Message<?> read(
            @PathVariable String auid,
            @PathVariable String alid,
            @RequestHeader("Authorization") String token
    ) {
        Token t = tokens.get(token.substring(7));
        if (Objects.equals(t.belong().toString(), auid)) return Message.unauthorized(null);

        return null;
    }
}
