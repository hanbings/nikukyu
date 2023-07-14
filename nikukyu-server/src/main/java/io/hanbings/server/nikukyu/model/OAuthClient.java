package io.hanbings.server.nikukyu.model;

import org.jetbrains.annotations.NotNull;

/**
 * OAuth 客户端数据
 *
 * @param ocid    OAuth 客户端 ID
 * @param created 创建时间
 * @param ouid    OAuth ID
 * @param secret  密钥
 * @param expire  过期时间
 */
@SuppressWarnings("SpellCheckingInspection")
public record OAuthClient(
        @NotNull String ocid,
        long created,
        @NotNull String ouid,
        @NotNull String secret,
        long expire
) {
}
