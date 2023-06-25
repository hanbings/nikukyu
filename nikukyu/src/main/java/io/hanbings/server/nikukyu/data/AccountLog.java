package io.hanbings.server.nikukyu.data;

import io.hanbings.server.nikukyu.content.AccountLogType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@SuppressWarnings("SpellCheckingInspection")
public record AccountLog(
        @NotNull @Id UUID alid,
        long create,
        @NotNull UUID auid,
        @NotNull String ip,
        @NotNull AccountLogType type
) {
}
