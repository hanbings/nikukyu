package io.hanbings.nikukyu.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * OAuth 客户端数据
 *
 * @param ocid    OAuth 客户端 ID
 * @param created 创建时间
 * @param ouid    OAuth ID
 * @param secret  密钥
 * @param expire  过期时间
 */

@Document("oauth_client")
@SuppressWarnings("SpellCheckingInspection")
public record OAuthClient(
        @Id @JsonProperty("oauth_client_id") @NotNull String ocid,
        @Field("created") @JsonProperty("created") long created,
        @Field("oauth_id") @JsonProperty("oauth_id") @NotNull String ouid,
        @Field("secret") @JsonProperty("secret") @NotNull String secret,
        @Field("expire") @JsonProperty("expire") long expire
) {
}