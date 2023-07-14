package io.hanbings.server.nikukyu.model;


import io.hanbings.server.nikukyu.content.AccessType;
import org.jetbrains.annotations.NotNull;

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
@SuppressWarnings("SpellCheckingInspection")
public record AccountOAuth(
        @NotNull String aoid,
        long created,
        @NotNull String auid,
        @NotNull String ouid,
        @NotNull List<AccessType> access
) {
}
