package io.hanbings.nikukyu.server.model;

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
 * @param ouid        OAuth ID
 * @param created     创建时间
 * @param auid        用户账户 ID
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
@SuppressWarnings("SpellCheckingInspection")
public record OAuth(
        @Id @JsonProperty("oauth_id") @NotNull String ouid,
        @Field("created") @JsonProperty("created") long created,
        @Field("account_id") @JsonProperty("account_id") @NotNull String auid,
        @Field("redirect") @JsonProperty("redirect") @NotNull Set<String> redirect,
        @Field("access") @JsonProperty("access") @NotNull Set<String> access,
        @Field("avatar") @JsonProperty("avatar") @NotNull String avatar,
        @Field("name") @JsonProperty("name") @NotNull String name,
        @Field("description") @JsonProperty("description") @Nullable String description,
        @Field("homepage") @JsonProperty("homepage") @Nullable String homepage,
        @Field("background") @JsonProperty("background") @NotNull String background,
        @Field("theme") @JsonProperty("theme") @NotNull String theme,
        @Field("policy") @JsonProperty("policy") @Nullable String policy,
        @Field("tos") @JsonProperty("tos") @NotNull String tos
) {
}