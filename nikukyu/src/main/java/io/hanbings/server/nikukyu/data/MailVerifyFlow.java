package io.hanbings.server.nikukyu.data;

public record MailVerifyFlow(
        String code,
        long expire,
        String email
) {
}
