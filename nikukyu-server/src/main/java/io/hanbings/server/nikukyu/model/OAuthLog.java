package io.hanbings.server.nikukyu.model;

import io.hanbings.server.nikukyu.content.OAuthLogType;
import org.jetbrains.annotations.NotNull;

/**
 * OAuth 日志数据
 *
 * @param olid    日志 ID
 * @param created 创建时间
 * @param ip      IP 地址
 * @param ouid    OAuth ID
 * @param type    日志类型
 */
@SuppressWarnings("SpellCheckingInspection")
public record OAuthLog(
        @NotNull String olid,
        long created,
        @NotNull String ouid,
        @NotNull String ip,
        @NotNull OAuthLogType type
) {
}
