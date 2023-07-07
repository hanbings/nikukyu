package io.hanbings.server.nikukyu.data;

import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.model.Account;
import io.hanbings.server.nikukyu.model.OAuth;
import io.hanbings.server.nikukyu.model.OAuthClient;

import java.util.List;

public record OAuthAuthorizeFlow(
        Account account,
        OAuth oauth,
        OAuthClient client,
        List<AccessType> access,
        String state
) {
}
