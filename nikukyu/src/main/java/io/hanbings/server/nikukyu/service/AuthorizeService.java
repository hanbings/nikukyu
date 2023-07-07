package io.hanbings.server.nikukyu.service;

import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.data.OAuthAuthorizeFlow;
import io.hanbings.server.nikukyu.model.Account;
import io.hanbings.server.nikukyu.model.OAuth;
import io.hanbings.server.nikukyu.model.OAuthClient;
import io.hanbings.server.nikukyu.utils.RandomUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthorizeService {
    static Map<String, OAuthAuthorizeFlow> authorizes = new ConcurrentHashMap<>();

    public String createOAuthAuthorizationFlow(
            Account account,
            OAuth oauth,
            OAuthClient client,
            List<AccessType> access,
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
