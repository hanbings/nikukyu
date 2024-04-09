package io.hanbings.nikukyu.server.security;

import io.hanbings.nikukyu.server.model.Account;
import io.hanbings.nikukyu.server.model.OAuth;
import io.hanbings.nikukyu.server.model.OAuthClient;

import java.util.Set;

public record OAuthAuthorizeFlow(
        Account account,
        OAuth oauth,
        OAuthClient client,
        Set<String> access,
        String state
) {
}
