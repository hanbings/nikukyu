package io.hanbings.server.nikukyu.data;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@SuppressWarnings("SpellCheckingInspection")
public record AccountAuthorization(
        @NotNull @Id UUID aaid,
        long create,
        @NotNull UUID auid,
        @NotNull String provider,
        @NotNull String openid
) {
}
