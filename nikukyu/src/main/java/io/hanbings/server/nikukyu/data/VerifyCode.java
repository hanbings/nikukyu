package io.hanbings.server.nikukyu.data;

public record VerifyCode(
        String code,
        long expire,
        String email
) {
}
