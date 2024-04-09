package io.hanbings.nikukyu.server.security;

import io.hanbings.flows.common.interfaces.Identify;
import io.hanbings.nikukyu.server.model.AccountAuthorization;

public record MailVerifyFlow(
        String code,
        long expire,
        String email,
        AccountAuthorization accountAuthorization,
        Identify identify
) {
}