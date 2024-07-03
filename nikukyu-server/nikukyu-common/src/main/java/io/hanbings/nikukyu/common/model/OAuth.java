package io.hanbings.nikukyu.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;

/**
 * 注册在平台内的 OAuth 服务商数据
 *
 * @param oauthId     OAuth ID
 * @param created     创建时间
 * @param createdBy   用户账户 ID
 * @param redirect    重定向地址
 * @param access      授权类型
 * @param avatar      头像
 * @param name        名称
 * @param description 描述
 * @param background  背景图片
 * @param theme       主题颜色
 * @param policy      隐私政策
 * @param tos         服务条款
 */

@Document("oauth")
public record OAuth(
        @Id @JsonProperty("oauthId") @NotNull String oauthId,
        @Field("created") @JsonProperty("created") long created,
        @Field("created_by") @JsonProperty("createdBy") @NotNull String createdBy,
        @Field("redirect") @JsonProperty("redirect") @NotNull Set<String> redirect,
        @Field("access") @JsonProperty("access") @NotNull Set<String> access,
        @Field("avatar") @JsonProperty("avatar") @Nullable String avatar,
        @Field("name") @JsonProperty("name") @NotNull String name,
        @Field("description") @JsonProperty("description") @Nullable String description,
        @Field("homepage") @JsonProperty("homepage") @Nullable String homepage,
        @Field("background") @JsonProperty("background") @Nullable String background,
        @Field("theme") @JsonProperty("theme") @Nullable String theme,
        @Field("policy") @JsonProperty("policy") @Nullable String policy,
        @Field("tos") @JsonProperty("tos") @Nullable String tos
) {
}