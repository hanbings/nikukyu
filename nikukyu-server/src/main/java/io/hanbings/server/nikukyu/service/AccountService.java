package io.hanbings.server.nikukyu.service;

import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.model.Account;
import io.hanbings.server.nikukyu.model.AccountAuthorization;
import io.hanbings.server.nikukyu.model.AccountLog;
import io.hanbings.server.nikukyu.model.AccountOAuth;
import io.hanbings.server.nikukyu.repository.AccountAuthorizationRepository;
import io.hanbings.server.nikukyu.repository.AccountLogRepository;
import io.hanbings.server.nikukyu.repository.AccountOAuthRepository;
import io.hanbings.server.nikukyu.repository.AccountRepository;
import io.hanbings.server.nikukyu.utils.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("SpellCheckingInspection")
public class AccountService {
    final AccountRepository accountRepository;
    final AccountLogRepository accountLogRepository;
    final AccountOAuthRepository accountOAuthRepository;
    final AccountAuthorizationRepository authorizationRepository;

    // Account Authorization
    public AccountAuthorization createAccountAuthorization(String auid, String provider, String openid) {
        return authorizationRepository.save(new AccountAuthorization(RandomUtils.uuid(), System.currentTimeMillis(), auid, provider, openid));
    }

    public AccountAuthorization getAccountAuthorizationWithOpenid(String openid) {
        List<AccountAuthorization> authorizations = authorizationRepository.findByOpenid(openid);

        return authorizations.isEmpty() ? null : authorizations.get(0);
    }

    public List<AccountAuthorization> getAccountAuthorizationsWithAuid(String auid) {
        return authorizationRepository.findByAuid(auid);
    }

    public AccountAuthorization getAccountAuthorizationWithAuid(String auid) {
        List<AccountAuthorization> authorizations = authorizationRepository.findByAuid(auid);

        return authorizations.isEmpty() ? null : authorizations.get(0);
    }

    public List<AccountAuthorization> getAccountAuthorizationsWithOpenid(String openid) {
        return authorizationRepository.findByOpenid(openid);
    }

    // Account Log
    public AccountLog createAccountLog(AccountLog log) {
        return accountLogRepository.save(log);
    }

    public List<AccountLog> getAccountLogsWithAuid(String auid) {
        return accountLogRepository.findByAuid(auid);
    }

    // Account OAuth
    public AccountOAuth createAccountOAuth(String auid, String ouid, List<AccessType> access) {
        return accountOAuthRepository.save(new AccountOAuth(RandomUtils.uuid(), System.currentTimeMillis(), auid, ouid, access));
    }

    public List<AccountAuthorization> getAccountOAuthsWithAuid(String auid) {
        return accountOAuthRepository.findByAuid(auid);
    }

    // Account
    public Account createAccount(boolean verified, String id, String nick, String avatar, String background, String color, String email) {
        return accountRepository.save(new Account(RandomUtils.uuid(), System.currentTimeMillis(), verified, id, nick, avatar, background, color, email));
    }

    public Account getAccountWithAuid(String auid) {
        return accountRepository.findByAuid(auid);
    }

    public Account getAccountWithEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public Account getAccountWithId(String id) {
        return accountRepository.findById(id);
    }
}
