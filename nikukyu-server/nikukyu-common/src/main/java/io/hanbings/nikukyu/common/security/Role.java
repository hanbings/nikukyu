package io.hanbings.nikukyu.common.security;

import io.hanbings.nikukyu.common.utils.ReflectionUtils;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Set;

public class Role {
    @Getter
    @Accessors(fluent = true)
    static Set<String> allPermissions;

    static {
        try {
            allPermissions = ReflectionUtils.getAllStaticPublicStrings(Permission.class);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("unable to get all permissions", e);
        }
    }
}