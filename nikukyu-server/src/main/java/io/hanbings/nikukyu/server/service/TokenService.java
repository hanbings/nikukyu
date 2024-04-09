package io.hanbings.nikukyu.server.service;

import io.hanbings.nikukyu.server.security.Token;
import io.hanbings.nikukyu.server.utils.RandomUtils;
import io.hanbings.nikukyu.server.utils.TimeUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {
    static Map<String, Token> tokens = new ConcurrentHashMap<>();

    public Token signature(String belong, long expire, Set<String> access) {
        Token token = new Token(
                RandomUtils.uuid(),
                belong,
                access,
                TimeUtils.getMilliUnixTime(),
                TimeUtils.getMilliUnixTime() + expire
        );

        tokens.put(token.token(), token);

        return token;
    }

    public Token parse(String header) {
        String authorization = header.substring(7);

        return tokens.get(authorization);
    }

    public Token get(String token) {
        return tokens.get(token);
    }

    public void revoke(String token) {
        tokens.remove(token);
    }

    public boolean checkAccess(String token, Set<String> access) {
        // 检查 Token 是否有权限
        Token t = tokens.get(token);

        return t.permissions().containsAll(access);
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