package io.hanbings.server.nikukyu.service;

import io.hanbings.flows.common.OAuth;
import io.hanbings.flows.common.interfaces.Access;
import io.hanbings.flows.common.interfaces.Identify;
import io.hanbings.flows.common.interfaces.Request;
import io.hanbings.flows.github.GithubOAuth;
import io.hanbings.server.nikukyu.config.Config;
import io.hanbings.server.nikukyu.config.OAuthConfig;
import io.hanbings.server.nikukyu.data.MailVerifyFlow;
import io.hanbings.server.nikukyu.data.Token;
import io.hanbings.server.nikukyu.model.AccountAuthorization;
import io.hanbings.server.nikukyu.utils.RandomUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginService {
    static Request.Proxy proxy;
    static Map<String, OAuth<? extends Access, ? extends Access.Wrong>> providers = new ConcurrentHashMap<>();

    // 缓存验证码与 Token 的对应关系 (验证码只能使用一次) Token - VerifyCode
    static Map<String, MailVerifyFlow> verifies = new ConcurrentHashMap<>();

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

                if (provider.proxy()) {
                    LoginService.providers.get(type.toString()).proxy(() -> LoginService.proxy);
                }
            }
        });
    }

    @Scheduled(fixedRate = 30000)
    public void updateVerifyFlow() {
        verifies.forEach((token, flow) -> {
            if (flow.expire() < System.currentTimeMillis()) {
                verifies.remove(token);
            }
        });
    }

    public String getOAuthLoginAccountAuthorize(String provider) {
        return providers.get(provider).authorize();
    }

    public OAuth<? extends Access, ? extends Access.Wrong> getOAuthProviders(String provider) {
        return providers.get(provider);
    }

    public MailVerifyFlow createMailVerifyFlow(Token token, String email, AccountAuthorization authorization, Identify identify) {
        MailVerifyFlow flow = new MailVerifyFlow(
                RandomUtils.strings(6),
                System.currentTimeMillis() + TokenService.Expire.MINUTE * 5,
                email,
                authorization,
                identify
        );

        verifies.put(token.token(), flow);

        return flow;
    }

    public MailVerifyFlow getVerifyFlow(Token token) {
        return verifies.get(token.token());
    }
}
