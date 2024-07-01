package io.hanbings.nikukyu.server.service;

import io.hanbings.flows.common.OAuth;
import io.hanbings.flows.common.interfaces.Access;
import io.hanbings.flows.common.interfaces.Identify;
import io.hanbings.flows.common.interfaces.Request;
import io.hanbings.flows.discord.DiscordOAuth;
import io.hanbings.flows.github.GithubOAuth;
import io.hanbings.flows.google.GoogleOAuth;
import io.hanbings.flows.microsoft.MicrosoftCommonOAuth;
import io.hanbings.nikukyu.common.model.AccountAuthorization;
import io.hanbings.nikukyu.common.security.MailVerifyFlow;
import io.hanbings.nikukyu.common.security.Token;
import io.hanbings.nikukyu.common.utils.RandomUtils;
import io.hanbings.nikukyu.common.utils.TimeUtils;
import io.hanbings.nikukyu.server.config.Config;
import io.hanbings.nikukyu.server.config.OAuthConfig;
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

                    case DISCORD -> LoginService.providers.put(type.toString(), new DiscordOAuth(
                            provider.clientId(),
                            provider.clientSecret(),
                            String.format("%s/login/oauth/%s/callback", config.getSite(), type),
                            List.of("identify", "email"),
                            Map.of()
                    ));

                    case GOOGLE -> LoginService.providers.put(type.toString(), new GoogleOAuth(
                            provider.clientId(),
                            provider.clientSecret(),
                            String.format("%s/login/oauth/%s/callback", config.getSite(), type),
                            List.of(
                                    "openid",
                                    "https://www.googleapis.com/auth/userinfo.email",
                                    "https://www.googleapis.com/auth/userinfo.profile"
                            ),
                            Map.of()
                    ));

                    case MICROSOFT -> LoginService.providers.put(type.toString(), new MicrosoftCommonOAuth(
                            provider.clientId(),
                            provider.clientSecret(),
                            String.format("%s/login/oauth/%s/callback", config.getSite(), type),
                            List.of("mail.read", "user.read"),
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
            if (flow.expire() < TimeUtils.getMilliUnixTime()) {
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
                RandomUtils.string(6),
                TimeUtils.getMilliUnixTime() + TokenService.Expire.MINUTE * 5,
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
