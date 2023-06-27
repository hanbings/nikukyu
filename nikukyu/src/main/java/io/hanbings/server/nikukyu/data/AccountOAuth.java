package io.hanbings.server.nikukyu.data;


import io.hanbings.server.nikukyu.content.AccessType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.UUID;

/**
 * 用户账户授权本平台 OAuth 授权数据
 *
 * @param aaid   授权 ID
 * @param create 创建时间
 * @param auid   用户账户 ID
 * @param ouid   OAuth ID
 * @param access 授权类型
 */
@SuppressWarnings("SpellCheckingInspection")
public record AccountOAuth(
        @NotNull @Id UUID aaid,
        long create,
        @NotNull UUID auid,
        @NotNull UUID ouid,
        @NotNull List<AccessType> access
) {
}
