package io.hanbings.nikukyu.server.data;

import java.util.Set;

public record OAuthDto(
        String name,
        Set<String> redirect,
        Set<String> access,
        String avatar,
        String description,
        String homepage,
        String background,
        String theme,
        String policy,
        String tos
) {
}