package io.hanbings.server.nikukyu.data;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;

import java.util.UUID;

/**
 * OAuth 客户端数据
 *
 * @param ocid     OAuth 客户端 ID
 * @param create   创建时间
 * @param ouid     OAuth ID
 * @param secret   密钥
 * @param expireIn 过期时间
 */
@SuppressWarnings("SpellCheckingInspection")
public record OAuthClient(
        @NotNull @Id UUID ocid,
        long create,
        @NotNull UUID ouid,
        @NotNull String secret,
        long expireIn
) {
}
