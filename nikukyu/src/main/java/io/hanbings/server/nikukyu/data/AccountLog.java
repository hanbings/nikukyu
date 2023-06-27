package io.hanbings.server.nikukyu.data;

import io.hanbings.server.nikukyu.content.AccountLogType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;

import java.util.UUID;

/**
 * 用户账户日志数据
 *
 * @param alid   日志 ID
 * @param create 创建时间
 * @param auid   用户账户 ID
 * @param ip     IP 地址
 * @param type   日志类型
 */
@SuppressWarnings("SpellCheckingInspection")
public record AccountLog(
        @NotNull @Id UUID alid,
        long create,
        @NotNull UUID auid,
        @NotNull String ip,
        @NotNull AccountLogType type
) {
}
