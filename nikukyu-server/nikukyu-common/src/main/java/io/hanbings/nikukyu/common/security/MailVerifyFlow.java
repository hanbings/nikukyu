package io.hanbings.nikukyu.common.security;

import io.hanbings.flows.common.interfaces.Identify;
import io.hanbings.nikukyu.common.model.AccountAuthorization;

public record MailVerifyFlow(
        String code,
        long expire,
        String email,
        AccountAuthorization accountAuthorization,
        Identify identify
) {
}