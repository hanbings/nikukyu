package io.hanbings.server.nikukyu.service;

import io.hanbings.server.nikukyu.data.Account;
import io.hanbings.server.nikukyu.data.AccountAuthorization;
import io.hanbings.server.nikukyu.data.AccountLog;
import io.hanbings.server.nikukyu.data.AccountOAuth;
import io.hanbings.server.nikukyu.repository.AccountAuthorizationRepository;
import io.hanbings.server.nikukyu.repository.AccountLogRepository;
import io.hanbings.server.nikukyu.repository.AccountOAuthRepository;
import io.hanbings.server.nikukyu.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@SuppressWarnings("SpellCheckingInspection")
public class AccountService {
    AccountAuthorizationRepository authorizations;
    AccountLogRepository logs;
    AccountOAuthRepository oauths;
    AccountRepository accounts;

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
