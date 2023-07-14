package io.hanbings.server.nikukyu.model;

import org.jetbrains.annotations.NotNull;

/**
 * 用户账户数据
 *
 * @param auid       用户账户 ID
 * @param created    创建时间
 * @param verified   是否已验证
 * @param id         用户 ID
 * @param nick       用户昵称
 * @param avatar     用户头像
 * @param background 背景图片
 * @param color      主题颜色
 * @param email      用户邮箱
 */
@SuppressWarnings("SpellCheckingInspection")
public record Account(
        @NotNull String auid,
        long created,
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
