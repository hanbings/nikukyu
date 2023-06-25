package io.hanbings.server.nikukyu.data;


import io.hanbings.server.nikukyu.content.AccessType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("SpellCheckingInspection")
public record AccountOAuth(
        @NotNull @Id UUID aaid,
        long create,
        @NotNull UUID auid,
        @NotNull UUID ouid,
        @NotNull List<AccessType> access
) {
}
