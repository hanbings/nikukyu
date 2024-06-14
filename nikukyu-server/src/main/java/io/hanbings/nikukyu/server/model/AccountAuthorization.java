package io.hanbings.nikukyu.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 用户已授权用于 OAuth 登录的第三方服务提供商数据
 *
 * @param accountAuthorizationId 授权 ID
 * @param created                创建时间
 * @param createdBy              用户账户 ID
 * @param provider               提供商名称
 * @param openid                 提供商用户 ID
 */

@Document("account_authorization")
public record AccountAuthorization(
        @Id @JsonProperty("accountAuthorizationId") @NotNull String accountAuthorizationId,
        @Field("created") @JsonProperty("created") long created,
        @Field("created_by") @JsonProperty("createdBy") @NotNull String createdBy,
        @Field("provider") @JsonProperty("provider") @NotNull String provider,
        @Field("openid") @JsonProperty("openid") @NotNull String openid
) {
}