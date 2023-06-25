package io.hanbings.server.nikukyu.model;

import io.hanbings.server.nikukyu.content.AccessType;

import java.util.List;
import java.util.UUID;

public record Token(
        String token,
        UUID belong,
        long time,
        long expire,
        List<AccessType> access
) {
}
