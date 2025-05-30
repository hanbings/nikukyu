package io.hanbings.nikukyu.server.security;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PermissionChecker implements StpInterface {
    public static Map<String, List<String>> temporaryPermissions = new HashMap<>();

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        String token = StpUtil.getTokenValueByLoginId(loginId);
        if (temporaryPermissions.containsKey(token)) return temporaryPermissions.get(token);

        return null;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return null;
    }
}