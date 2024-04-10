package io.hanbings.nikukyu.server.security;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public record Token(
        @JsonProperty("token") String token,
        @JsonProperty("belong") String belong,
        @JsonProperty("permissions") Set<String> permissions,
        @JsonProperty("expire") long expire,
        @JsonProperty("created") long created
) {
    @SuppressWarnings("SpellCheckingInspection")
    public static String Custom_Header = "X-Nikukyu-Token";
}