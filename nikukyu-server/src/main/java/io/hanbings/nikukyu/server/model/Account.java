package io.hanbings.nikukyu.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 用户账户数据
 *
 * @param auid       用户账户 ID
 * @param created    创建时间
 * @param verified   是否已验证
 * @param id         用户 ID
 * @param nickname   用户昵称
 * @param avatar     用户头像
 * @param background 背景图片
 * @param color      主题颜色
 * @param email      用户邮箱
 */
@Document("account")
@SuppressWarnings("SpellCheckingInspection")
public record Account(
        @Id @JsonProperty("account_id") @NotNull String auid,
        @Field("created") @JsonProperty("created") long created,
        @Field("verified") @JsonProperty("verified") boolean verified,
        @Field("id") @JsonProperty("id") @NotNull String id,
        @Field("nickname") @JsonProperty("nickname") @NotNull String nickname,
        @Field("avatar") @JsonProperty("avatar") @NotNull String avatar,
        @Field("email") @JsonProperty("email") @NotNull String email,
        @Field("background") @JsonProperty("background") @NotNull String background,
        @Field("color") @JsonProperty("color") @NotNull String color
) {
}