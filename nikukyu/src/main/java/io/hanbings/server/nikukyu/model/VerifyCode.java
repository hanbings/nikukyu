package io.hanbings.server.nikukyu.model;

public record VerifyCode(
        String code,
        long expire,
        String email
) {
}
