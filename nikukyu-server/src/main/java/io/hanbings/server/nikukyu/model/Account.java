package io.hanbings.server.nikukyu.model;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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

@Document("account")
@SuppressWarnings("SpellCheckingInspection")
public record Account(
        @NotNull @Id String auid,
        long created,
        boolean verified,

        // 用户 ID
        String id,
        // 用户昵称
        String nick,
        // 用户头像
        String avatar,
        // 背景图片
        String background,
        // 主题颜色
        String color,
        // 用户邮箱
        String email
) {
}
