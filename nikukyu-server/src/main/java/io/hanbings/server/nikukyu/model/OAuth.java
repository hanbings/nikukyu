package io.hanbings.server.nikukyu.model;

import io.hanbings.server.nikukyu.content.AccessType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

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
        @NotNull String ouid,
        long created,
        @NotNull String auid,
        @NotNull List<String> redirect,
        @NotNull List<AccessType> access,
        @NotNull String avatar,
        @NotNull String name,
        @NotNull String description,
        @NotNull String homepage,
        @NotNull String background,
        @NotNull String theme,
        @NotNull String policy,
        @NotNull String tos
) {
}
