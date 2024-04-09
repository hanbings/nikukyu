package io.hanbings.nikukyu.server.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

/**
 * Session 用户会话信息
 *
 * @param token     凭据 包含权限信息
 * @param device    设备名
 * @param ip        IP 地址
 * @param useragent 用户识标头
 */
public record Session(
        @NotNull @JsonProperty("token") Token token,
        @NotNull @JsonProperty("device") String device,
        @NotNull @JsonProperty("ip") String ip,
        @NotNull @JsonProperty("useragent") String useragent
) {
}
