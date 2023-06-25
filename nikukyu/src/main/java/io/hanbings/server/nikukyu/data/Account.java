package io.hanbings.server.nikukyu.data;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@SuppressWarnings("SpellCheckingInspection")
public record Account(
        @NotNull @Id UUID auid,
        long create,
        boolean verified,

        // 用户 ID
        @NotNull String id,
        // 用户昵称
        @NotNull String nick,
        // 用户头像
        @NotNull String avatar,
        // 背景图片
        @NotNull String background,
        // 主题颜色
        @NotNull String color,
        // 用户邮箱
        @NotNull String email
) {
}
