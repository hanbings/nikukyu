package io.hanbings.server.nikukyu.data;

import io.hanbings.flows.common.interfaces.Identify;
import io.hanbings.server.nikukyu.model.AccountAuthorization;

public record MailVerifyFlow(
        String code,
        long expire,
        String email,
        AccountAuthorization accountAuthorization,
        Identify identify
) {
}
