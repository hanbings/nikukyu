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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("SpellCheckingInspection")
public class AccountService {
    final AccountRepository accountRepository;
    final AccountLogRepository accountLogRepository;
    final AccountOAuthRepository accountOAuthRepository;
    final AccountAuthorizationRepository authorizationRepository;

    // Account Authorization
    public AccountAuthorization createAccountAuthorization(UUID auid, String provider, String openid) {
        return authorizationRepository.save(new AccountAuthorization(UUID.randomUUID(), System.currentTimeMillis(), auid, provider, openid));
    }

    public AccountAuthorization getAccountAuthorizationWithOpenid(String openid) {
        return authorizationRepository.findByOpenid(openid).get(0);
    }

    public List<AccountAuthorization> getAccountAuthorizationsWithAuid(UUID auid) {
        return authorizationRepository.findByAuid(auid);
    }

    public AccountAuthorization getAccountAuthorizationWithAuid(UUID auid) {
        return authorizationRepository.findByAuid(auid).get(0);
    }

    public List<AccountAuthorization> getAccountAuthorizationsWithOpenid(String openid) {
        return authorizationRepository.findByOpenid(openid);
    }

    // Account Log
    public AccountLog createAccountLog(AccountLog log) {
        return accountLogRepository.save(log);
    }

    public List<AccountLog> getAccountLogsWithAuid(UUID auid) {
        return accountLogRepository.findByAuid(auid);
    }

    // Account OAuth
    public AccountOAuth createAccountOAuth(UUID auid, UUID ouid, List<AccessType> access) {
        return accountOAuthRepository.save(new AccountOAuth(UUID.randomUUID(), System.currentTimeMillis(), auid, ouid, access));
    }

    public List<AccountAuthorization> getAccountOAuthsWithAuid(UUID auid) {
        return accountOAuthRepository.findByAuid(auid);
    }

    // Account
    public Account createAccount(boolean verified, String id, String nick, String avatar, String background, String color, String email) {
        return accountRepository.save(new Account(UUID.randomUUID(), System.currentTimeMillis(), verified, id, nick, avatar, background, color, email));
    }

    public Account getAccountWithAuid(UUID auid) {
        return accountRepository.findByAuid(auid);
    }

    public Account getAccountWithEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public Account getAccountWithId(String id) {
        return accountRepository.findById(id);
    }
}
