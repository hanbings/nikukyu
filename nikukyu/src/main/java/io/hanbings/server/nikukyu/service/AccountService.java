package io.hanbings.server.nikukyu.service;

import io.hanbings.flows.common.OAuth;
import io.hanbings.flows.common.interfaces.Access;
import io.hanbings.flows.common.interfaces.Request;
import io.hanbings.flows.github.GithubOAuth;
import io.hanbings.server.nikukyu.config.Config;
import io.hanbings.server.nikukyu.config.OAuthConfig;
import io.hanbings.server.nikukyu.model.Account;
import io.hanbings.server.nikukyu.model.AccountAuthorization;
import io.hanbings.server.nikukyu.model.AccountLog;
import io.hanbings.server.nikukyu.model.AccountOAuth;
import io.hanbings.server.nikukyu.repository.AccountAuthorizationRepository;
import io.hanbings.server.nikukyu.repository.AccountLogRepository;
import io.hanbings.server.nikukyu.repository.AccountOAuthRepository;
import io.hanbings.server.nikukyu.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.net.Proxy;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@SuppressWarnings("SpellCheckingInspection")
public class AccountService {
    static Request.Proxy proxy;
    static Map<String, OAuth<? extends Access, ? extends Access.Wrong>> providers = new ConcurrentHashMap<>();
    AccountAuthorizationRepository authorizations;
    AccountLogRepository logs;
    AccountOAuthRepository oauths;
    AccountRepository accounts;

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    public AccountService(
            Config config,
            OAuthConfig oauth,
            AccountAuthorizationRepository authorizations,
            AccountRepository accounts,
            AccountOAuthRepository oauths,
            AccountLogRepository logs
    ) {
        this.authorizations = authorizations;
        this.logs = logs;
        this.oauths = oauths;
        this.accounts = accounts;

        // init oauth service
        AccountService.proxy = new Request.Proxy(
                Proxy.Type.HTTP,
                oauth.proxy().host(),
                Integer.parseInt(oauth.proxy().port()),
                oauth.proxy().username(),
                oauth.proxy().password()
        );

        oauth.providers().forEach((type, provider) -> {
            if (provider.enable()) {
                switch (type) {
                    case GITHUB -> AccountService.providers.put(type.toString(), new GithubOAuth(
                            provider.clientId(),
                            provider.clientSecret(),
                            String.format("%s/login/oauth/%s/callback", config.getSite(), type),
                            List.of("read:user", "user:email"),
                            Map.of()
                    ));
                }
            }

            if (provider.proxy()) {
                AccountService.providers.get(provider.toString()).proxy(() -> AccountService.proxy);
            }
        });
    }

    public String getOAuthLoginAccountAuthorize(String provider) {
        return providers.get(provider).authorize();
    }

    public OAuth<? extends Access, ? extends Access.Wrong> getOAuthProviders(String provider) {
        return providers.get(provider);
    }

    // Account Authorization
    public AccountAuthorization createAccountAuthorization(AccountAuthorization authorization) {
        return authorizations.save(authorization);
    }

    public AccountAuthorization getAccountAuthorizationWithOpenid(String openid) {
        return authorizations.findByOpenid(openid).get(0);
    }

    public List<AccountAuthorization> getAccountAuthorizationsWithAuid(UUID auid) {
        return authorizations.findByAuid(auid);
    }

    public AccountAuthorization getAccountAuthorizationWithAuid(UUID auid) {
        return authorizations.findByAuid(auid).get(0);
    }

    public List<AccountAuthorization> getAccountAuthorizationsWithOpenid(String openid) {
        return authorizations.findByOpenid(openid);
    }

    // Account Log
    public AccountLog createAccountLog(AccountLog log) {
        return logs.save(log);
    }

    public List<AccountLog> getAccountLogsWithAuid(UUID auid) {
        return logs.findByAuid(auid);
    }

    // Account OAuth
    public AccountOAuth createAccountOAuth(AccountOAuth oauth) {
        return oauths.save(oauth);
    }

    public List<AccountAuthorization> getAccountOAuthsWithAuid(UUID auid) {
        return oauths.findByAuid(auid);
    }

    // Account
    @SuppressWarnings("UnusedReturnValue")
    public Account createAccount(Account account) {
        return accounts.save(account);
    }

    public Account getAccountWithAuid(UUID auid) {
        return accounts.findByAuid(auid);
    }

    public Account getAccountWithEmail(String email) {
        return accounts.findByEmail(email);
    }

    public Account getAccountWithId(String id) {
        return accounts.findById(id);
    }
}
