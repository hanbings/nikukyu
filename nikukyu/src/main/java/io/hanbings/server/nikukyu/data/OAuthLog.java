package io.hanbings.server.nikukyu.data;

import io.hanbings.server.nikukyu.content.OAuthLogType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@SuppressWarnings("SpellCheckingInspection")
public record OAuthLog(
        @NotNull @Id UUID olid,
        long create,
        @NotNull UUID ouid,
        @NotNull OAuthLogType type
) {
}
