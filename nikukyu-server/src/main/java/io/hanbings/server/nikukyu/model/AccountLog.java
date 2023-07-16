package io.hanbings.server.nikukyu.model;

import io.hanbings.server.nikukyu.content.AccountLogType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 用户账户日志数据
 *
 * @param alid    日志 ID
 * @param created 创建时间
 * @param auid    用户账户 ID
 * @param ip      IP 地址
 * @param type    日志类型
 */

@Document("account_log")
@SuppressWarnings("SpellCheckingInspection")
public record AccountLog(
        @NotNull String alid,
        long created,
        @NotNull String auid,
        @NotNull String ip,
        @NotNull AccountLogType type
) {
}
