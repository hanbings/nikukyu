package io.hanbings.server.nikukyu.service;

import io.hanbings.server.nikukyu.model.Authorize;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OAuthAuthorizeService {
    static Map<String, Authorize> authorizes = new ConcurrentHashMap<>();

    public Authorize create(String code, Authorize authorize) {
        return authorizes.put(code, authorize);
    }

    public Authorize get(String code) {
        return authorizes.remove(code);
    }
}
