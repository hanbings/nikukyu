package io.hanbings.nikukyu.server.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public enum AccessType {
    // 在 OAuth 验证中未创建 OAuth 记录和绑定账号发起邮件验证的权限
    OAUTH_EMAIL_VERIFY,
    // 发起邮件验证的权限
    EMAIL_VERIFY,


    // 账号相关
    ACCOUNT_READ,
    ACCOUNT_WRITE,
    ACCOUNT_SETTINGS_READ,
    ACCOUNT_SETTINGS_WRITE,
    ACCOUNT_DESTROY,
    ACCOUNT_AUTHORIZATION_READ,
    ACCOUNT_AUTHORIZATION_WRITE,
    ACCOUNT_AUTHORIZATION_DESTROY,
    // 查看账号内 OAuth 提供商的权限
    ACCOUNT_OAUTH_READ,
    ACCOUNT_OAUTH_WRITE,
    // 查看日志的权限
    ACCOUNT_LOG_READ,
    ACCOUNT_LOG_WRITE,


    // OAuth 相关
    OAUTH_READ,
    OAUTH_WRITE,
    OAUTH_DESTROY,
    OAUTH_CLIENT_READ,
    OAUTH_CLIENT_WRITE,
    OAUTH_CLIENT_DESTROY,
    OAUTH_LOG_READ,
    OAUTH_LOG_WRITE,

    // OAuth 授权相关
    OAUTH_AUTHORIZE,
    OAUTH_TOKEN,
    OAUTH_REFRESH,
    OAUTH_REVOKE;

    public static List<AccessType> all() {
        return new ArrayList<>() {{
            addAll(
                    Arrays.stream(AccessType.values())
                            .filter(e -> !e.equals(AccessType.EMAIL_VERIFY))
                            .filter(e -> !e.equals(AccessType.OAUTH_EMAIL_VERIFY))
                            .toList()
            );
        }};
    }

    public static List<AccessType> parse(String scope) {
        if (scope == null || scope.isEmpty()) {
            return List.of();
        }

        String[] scopes = scope.split(",");
        List<AccessType> result = new ArrayList<>();
        for (String s : scopes) {
            try {
                result.add(AccessType.valueOf(s.toUpperCase(Locale.ROOT).replaceAll("\\.", "_")));
            } catch (Exception ignore) {
            }
        }

        return result;
    }

    public static String parse(List<AccessType> access) {
        if (access.isEmpty()) return "";

        StringBuilder builder = new StringBuilder();

        access.forEach(a -> {
            builder.append(a.toString());
            builder.append(", ");
        });

        return builder.substring(0, builder.length() - 2);
    }

    public String toString() {
        return this.name().toLowerCase(Locale.ROOT).replaceAll("_", ".");
    }
}