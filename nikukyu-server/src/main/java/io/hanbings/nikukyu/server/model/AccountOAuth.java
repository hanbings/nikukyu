package io.hanbings.nikukyu.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.hanbings.nikukyu.server.content.AccessType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * 用户账户授权本平台 OAuth 授权数据
 *
 * @param aoid    授权 ID
 * @param created 创建时间
 * @param auid    用户账户 ID
 * @param ouid    OAuth ID
 * @param access  授权类型
 */

@Document("account_oauth")
@SuppressWarnings("SpellCheckingInspection")
public record AccountOAuth(
        @Id @JsonProperty("account_oauth_id") @NotNull String aoid,
        @Field("created") @JsonProperty("created") long created,
        @Field("account_id") @JsonProperty("account_id") @NotNull String auid,
        @Field("oauth_id") @JsonProperty("oauth_id") @NotNull String ouid,
        @Field("access") @JsonProperty("access") @NotNull List<AccessType> access
) {
}