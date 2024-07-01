package io.hanbings.nikukyu.common.security;

import io.hanbings.nikukyu.common.model.Account;
import io.hanbings.nikukyu.common.model.OAuth;
import io.hanbings.nikukyu.common.model.OAuthClient;

import java.util.Set;

public record OAuthAuthorizeFlow(
        Account account,
        OAuth oauth,
        OAuthClient client,
        Set<String> access,
        String state
) {
}
