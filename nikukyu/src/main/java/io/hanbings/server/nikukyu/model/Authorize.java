package io.hanbings.server.nikukyu.model;

import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.data.Account;
import io.hanbings.server.nikukyu.data.OAuth;
import io.hanbings.server.nikukyu.data.OAuthClient;

import java.util.List;

public record Authorize(
        Account account,
        OAuth oauth,
        OAuthClient client,
        List<AccessType> access,
        String state
) {
}
