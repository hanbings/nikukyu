package io.hanbings.nikukyu.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * OAuth 客户端数据
 *
 * @param oauthClientId OAuth 客户端 ID
 * @param created       创建时间
 * @param createdBy     OAuth ID
 * @param secret        密钥
 * @param expire        过期时间
 */

@Document("oauth_client")
public record OAuthClient(
        @Id @JsonProperty("oauthClientId") @NotNull String oauthClientId,
        @Field("created") @JsonProperty("created") long created,
        @Field("created_by") @JsonProperty("createdBy") @NotNull String createdBy,
        @Field("secret") @JsonProperty("secret") @NotNull String secret,
        @Field("expire") @JsonProperty("expire") long expire
) {
}