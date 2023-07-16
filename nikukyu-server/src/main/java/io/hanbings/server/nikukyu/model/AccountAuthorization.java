package io.hanbings.server.nikukyu.model;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.mapping.Document;

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
@Document("account_authorization")
public record AccountAuthorization(
        @NotNull String aaid,
        long created,
        @NotNull String auid,
        @NotNull String provider,
        @NotNull String openid
) {
}
