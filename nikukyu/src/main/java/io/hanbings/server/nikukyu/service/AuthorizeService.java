package io.hanbings.server.nikukyu.service;

import io.hanbings.server.nikukyu.data.Authorize;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthorizeService {
    static Map<String, Authorize> authorizes = new ConcurrentHashMap<>();

    public Authorize createOAuthAuthorizationFlow(String code, Authorize authorize) {
        return authorizes.put(code, authorize);
    }

    public Authorize getOAuthAuthorizationFlow(String code) {
        return authorizes.remove(code);
    }
}
