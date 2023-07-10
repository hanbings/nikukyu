package io.hanbings.server.nikukyu.model;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;

import java.util.UUID;

/**
 * 用户已授权用于 OAuth 登录的第三方服务提供商数据
 *
 * @param aaid     授权 ID
 * @param created  创建时间
 * @param auid     用户账户 ID
 * @param provider 提供商名称
 * @param openid   提供商用户 ID
 */
@SuppressWarnings("SpellCheckingInspection")
public record AccountAuthorization(
        @NotNull @Id UUID aaid,
        long created,
        @NotNull UUID auid,
        @NotNull String provider,
        @NotNull String openid
) {
}
