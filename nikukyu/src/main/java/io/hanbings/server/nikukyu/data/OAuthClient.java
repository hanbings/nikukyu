package io.hanbings.server.nikukyu.data;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@SuppressWarnings("SpellCheckingInspection")
public record OAuthClient(
        @NotNull @Id UUID ocid,
        long create,
        @NotNull UUID ouid,
        @NotNull String secret,
        long expireIn
) {
}
