package io.hanbings.server.nikukyu.service;

import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.data.Token;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {
    static Map<String, Token> tokens = new ConcurrentHashMap<>();

    public Token signature(UUID belong, long expire, List<AccessType> access) {
        Token token = new Token(
                UUID.randomUUID().toString(),
                belong,
                System.currentTimeMillis(),
                System.currentTimeMillis() + expire,
                access
        );

        tokens.put(token.token(), token);

        return token;
    }

    public Token get(String token) {
        return null;
    }

    public boolean revoke(String token) {
        return false;
    }

    public boolean check(String token, AccessType[] access) {
        // 检查 Token 是否在有效期内
        // 检查 Token 是否有权限

        return true;
    }

    public static class Expire {
        // 一分钟
        public static final long MINUTE = 60 * 1000;
        // 一小时
        public static final long HOUR = 60 * MINUTE;
        // 一天
        public static final long DAY = 24 * HOUR;
        // 一周
        public static final long WEEK = 7 * DAY;
        // 一月
        public static final long MONTH = 30 * DAY;
    }
}
