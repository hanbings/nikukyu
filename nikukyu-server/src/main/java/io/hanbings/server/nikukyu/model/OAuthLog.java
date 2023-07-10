package io.hanbings.server.nikukyu.model;

import io.hanbings.server.nikukyu.content.OAuthLogType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;

import java.util.UUID;

/**
 * OAuth 日志数据
 *
 * @param olid    日志 ID
 * @param created 创建时间
 * @param ouid    OAuth ID
 * @param type    日志类型
 */
@SuppressWarnings("SpellCheckingInspection")
public record OAuthLog(
        @NotNull @Id UUID olid,
        long created,
        @NotNull UUID ouid,
        @NotNull OAuthLogType type
) {
}
