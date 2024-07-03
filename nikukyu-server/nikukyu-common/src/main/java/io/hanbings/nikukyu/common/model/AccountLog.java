package io.hanbings.nikukyu.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 用户账户日志数据
 *
 * @param accountLogId 日志 ID
 * @param created      创建时间
 * @param createdBy    用户账户 ID
 * @param ip           IP 地址
 * @param type         日志类型
 */

@Document("account_log")
public record AccountLog(
        @Id @JsonProperty("accountLogId") @NotNull String accountLogId,
        @Field("created") @JsonProperty("created") long created,
        @Field("created_by") @JsonProperty("createdBy") @NotNull String createdBy,
        @Field("ip") @JsonProperty("ip") @NotNull String ip,
        @Field("type") @JsonProperty("type") @NotNull String type
) {
}