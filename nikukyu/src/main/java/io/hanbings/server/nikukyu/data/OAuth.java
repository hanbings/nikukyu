package io.hanbings.server.nikukyu.data;

import io.hanbings.server.nikukyu.content.AccessType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("SpellCheckingInspection")
public record OAuth(
        @NotNull @Id UUID ouid,
        long create,
        @NotNull UUID auid,
        @NotNull List<String> redirect,
        @NotNull List<AccessType> access,
        @NotNull String avatar,
        @NotNull String name,
        @NotNull String description,
        @NotNull String background,
        @NotNull String theme,
        @NotNull String policy,
        @NotNull String tos
) {
}
