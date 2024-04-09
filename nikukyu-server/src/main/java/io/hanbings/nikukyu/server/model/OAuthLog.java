package io.hanbings.nikukyu.server.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * OAuth 日志数据
 *
 * @param olid    日志 ID
 * @param created 创建时间
 * @param ip      IP 地址
 * @param ouid    OAuth ID
 * @param type    日志类型
 */

@Document("oauth_log")
@SuppressWarnings("SpellCheckingInspection")
public record OAuthLog(
        @Id @JsonProperty("oauth_log_id") @NotNull String olid,
        @Field("created") @JsonProperty("created") long created,
        @Field("oauth_id") @JsonProperty("oauth_id") @NotNull String ouid,
        @Field("ip") @JsonProperty("ip") @NotNull String ip,
        @Field("type") @JsonProperty("type") @NotNull String type
) {
}