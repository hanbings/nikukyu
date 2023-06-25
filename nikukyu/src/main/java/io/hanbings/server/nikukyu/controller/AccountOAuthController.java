package io.hanbings.server.nikukyu.controller;

import io.hanbings.server.nikukyu.service.AccountService;
import io.hanbings.server.nikukyu.service.TokenService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountOAuthController {
    final TokenService tokens;
    final AccountService accounts;
}
