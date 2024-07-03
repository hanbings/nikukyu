package io.hanbings.nikukyu.common.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * OAuth 日志数据
 *
 * @param oauthLogId 日志 ID
 * @param created    创建时间
 * @param ip         IP 地址
 * @param createdBy  OAuth ID
 * @param type       日志类型
 */

@Document("oauth_log")
public record OAuthLog(
        @Id @JsonProperty("oauthLogId") @NotNull String oauthLogId,
        @Field("created") @JsonProperty("created") long created,
        @Field("created_by") @JsonProperty("createdBy") @NotNull String createdBy,
        @Field("ip") @JsonProperty("ip") @NotNull String ip,
        @Field("type") @JsonProperty("type") @NotNull String type
) {
}