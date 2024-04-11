package io.hanbings.nikukyu.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;

/**
 * 用户账户授权本平台 OAuth 授权数据
 *
 * @param accountOAuthId 授权 ID
 * @param created        创建时间
 * @param createdBy      用户账户 ID
 * @param oAuthId        OAuth ID
 * @param access         授权类型
 */

@Document("account_oauth")
public record AccountOAuth(
        @Id @JsonProperty("account_oauth_id") @NotNull String accountOAuthId,
        @Field("created") @JsonProperty("created") long created,
        @Field("created_by") @JsonProperty("created_by") @NotNull String createdBy,
        @Field("oauth_id") @JsonProperty("oauth_id") @NotNull String oAuthId,
        @Field("access") @JsonProperty("access") @NotNull Set<String> access
) {
}