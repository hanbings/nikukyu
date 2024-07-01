package io.hanbings.nikukyu.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 用户账户数据
 *
 * @param accountId  用户账户 ID
 * @param created    创建时间
 * @param verified   是否已验证
 * @param username   用户名
 * @param nickname   用户昵称
 * @param avatar     用户头像
 * @param background 背景图片
 * @param color      主题颜色
 * @param email      用户邮箱
 */
@Document("account")
public record Account(
        @Id @JsonProperty("account_id") @NotNull String accountId,
        @Field("created") @JsonProperty("created") long created,
        @Field("verified") @JsonProperty("verified") boolean verified,
        @Field("username") @JsonProperty("username") @Nullable String username,
        @Field("nickname") @JsonProperty("nickname") @Nullable String nickname,
        @Field("avatar") @JsonProperty("avatar") @Nullable String avatar,
        @Field("email") @JsonProperty("email") @NotNull String email,
        @Field("background") @JsonProperty("background") @Nullable String background,
        @Field("color") @JsonProperty("color") @Nullable String color
) {
}