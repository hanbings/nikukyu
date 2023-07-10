package io.hanbings.server.nikukyu.model;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;

import java.util.UUID;

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
        @NotNull @Id UUID ocid,
        long created,
        @NotNull UUID ouid,
        @NotNull String secret,
        long expire
) {
}
