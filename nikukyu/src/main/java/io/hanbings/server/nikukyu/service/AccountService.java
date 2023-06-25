package io.hanbings.server.nikukyu.service;

import io.hanbings.server.nikukyu.data.Account;
import io.hanbings.server.nikukyu.data.AccountAuthorization;
import io.hanbings.server.nikukyu.data.AccountLog;
import io.hanbings.server.nikukyu.repository.AccountAuthorizationRepository;
import io.hanbings.server.nikukyu.repository.AccountLogRepository;
import io.hanbings.server.nikukyu.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@SuppressWarnings("SpellCheckingInspection")
public class AccountService {
    AccountRepository accounts;
    AccountLogRepository logs;
    AccountAuthorizationRepository authorizations;

    public Account createAccount(Account account) {
        return accounts.save(account);
    }

    public AccountAuthorization createAccountAuthorization(AccountAuthorization authorization) {
        return authorizations.save(authorization);
    }

    public Account getAccountWithAuid(UUID auid) {
        return accounts.findByAuid(auid);
    }

    public AccountAuthorization getAccountAuthorizationWithOpenid(String openid) {
        return authorizations.findByOpenid(openid).get(0);
    }

    public List<AccountAuthorization> getAccountAuthorizationsWithAuid(UUID auid) {
        return authorizations.findByAuid(auid);
    }

    public List<AccountLog> getAccountLogsWithAuid(UUID auid) {
        return logs.findByAuid(auid);
    }
}
