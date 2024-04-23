package io.hanbings.nikukyu.server.service;

import io.hanbings.nikukyu.server.model.Account;
import io.hanbings.nikukyu.server.model.OAuth;
import io.hanbings.nikukyu.server.model.OAuthClient;
import io.hanbings.nikukyu.server.security.OAuthAuthorizeFlow;
import io.hanbings.nikukyu.server.utils.RandomUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthorizeService {
    static Map<String, OAuthAuthorizeFlow> authorizes = new ConcurrentHashMap<>();

    public String createOAuthAuthorizationFlow(
            Account account,
            OAuth oauth,
            OAuthClient client,
            Set<String> access,
            String state
    ) {
        // 创建授权码
        String code = RandomUtils.uuid();

        OAuthAuthorizeFlow oAuthAuthorizeFlow = new OAuthAuthorizeFlow(account, oauth, client, access, state);

        authorizes.put(code, oAuthAuthorizeFlow);

        return code;
    }

    public OAuthAuthorizeFlow getOAuthAuthorizationFlow(String code) {
        return authorizes.remove(code);
    }
}
