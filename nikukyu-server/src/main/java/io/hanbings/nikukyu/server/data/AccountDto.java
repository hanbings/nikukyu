package io.hanbings.nikukyu.server.data;

public record AccountDto(
        String nickname,
        String avatar,
        String background,
        String color
) {
}
