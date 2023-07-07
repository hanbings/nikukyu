package io.hanbings.server.nikukyu.service;

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
    final AccountAuthorizationRepository authorizationRepository;
    final AccountLogRepository accountLogRepository;
    final AccountOAuthRepository accountOAuthRepository;
    final AccountRepository accountRepository;

    // Account Authorization
    public AccountAuthorization createAccountAuthorization(AccountAuthorization authorization) {
        return authorizationRepository.save(authorization);
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
    public AccountOAuth createAccountOAuth(AccountOAuth oauth) {
        return accountOAuthRepository.save(oauth);
    }

    public List<AccountAuthorization> getAccountOAuthsWithAuid(UUID auid) {
        return accountOAuthRepository.findByAuid(auid);
    }

    // Account
    @SuppressWarnings("UnusedReturnValue")
    public Account createAccount(Account account) {
        return accountRepository.save(account);
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
