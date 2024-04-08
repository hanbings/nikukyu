package io.hanbings.nikukyu.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
        @Id @JsonProperty("account_authorization_id") @NotNull String aaid,
        @Field("created") @JsonProperty("created") long created,
        @Field("account_id") @JsonProperty("account_id") @NotNull String auid,
        @Field("provider") @JsonProperty("provider") @NotNull String provider,
        @Field("openid") @JsonProperty("openid") @NotNull String openid
) {
}