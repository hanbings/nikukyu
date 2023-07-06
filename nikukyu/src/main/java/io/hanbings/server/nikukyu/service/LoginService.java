package io.hanbings.server.nikukyu.service;

import io.hanbings.flows.common.OAuth;
import io.hanbings.flows.common.interfaces.Access;
import io.hanbings.flows.common.interfaces.Request;
import io.hanbings.flows.github.GithubOAuth;
import io.hanbings.server.nikukyu.config.Config;
import io.hanbings.server.nikukyu.config.OAuthConfig;
import org.springframework.stereotype.Service;

import java.net.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginService {
    static Request.Proxy proxy;
    static Map<String, OAuth<? extends Access, ? extends Access.Wrong>> providers = new ConcurrentHashMap<>();

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    public LoginService(Config config, OAuthConfig oauth) {
        // init oauth service
        LoginService.proxy = new Request.Proxy(
                Proxy.Type.HTTP,
                oauth.proxy().host(),
                Integer.parseInt(oauth.proxy().port()),
                oauth.proxy().username(),
                oauth.proxy().password()
        );

        oauth.providers().forEach((type, provider) -> {
            if (provider.enable()) {
                switch (type) {
                    case GITHUB -> LoginService.providers.put(type.toString(), new GithubOAuth(
                            provider.clientId(),
                            provider.clientSecret(),
                            String.format("%s/login/oauth/%s/callback", config.getSite(), type),
                            List.of("read:user", "user:email"),
                            Map.of()
                    ));
                }
            }

            if (provider.proxy()) {
                LoginService.providers.get(provider.toString()).proxy(() -> LoginService.proxy);
            }
        });
    }

    public String getOAuthLoginAccountAuthorize(String provider) {
        return providers.get(provider).authorize();
    }

    public OAuth<? extends Access, ? extends Access.Wrong> getOAuthProviders(String provider) {
        return providers.get(provider);
    }
}
