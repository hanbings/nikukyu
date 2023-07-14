package io.hanbings.server.nikukyu.data;

import io.hanbings.server.nikukyu.content.AccessType;

import java.util.List;

public record Token(
        String token,
        String belong,
        long created,
        long expire,
        List<AccessType> access
) {
}
