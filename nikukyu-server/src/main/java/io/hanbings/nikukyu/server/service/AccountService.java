package io.hanbings.nikukyu.server.service;

import io.hanbings.nikukyu.server.model.Account;
import io.hanbings.nikukyu.server.model.AccountAuthorization;
import io.hanbings.nikukyu.server.model.AccountLog;
import io.hanbings.nikukyu.server.model.AccountOAuth;
import io.hanbings.nikukyu.server.repository.AccountAuthorizationRepository;
import io.hanbings.nikukyu.server.repository.AccountLogRepository;
import io.hanbings.nikukyu.server.repository.AccountOAuthRepository;
import io.hanbings.nikukyu.server.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    static int MAX_PAGE_SIZE = 20;
    final AccountAuthorizationRepository accountAuthorizationRepository;
    final AccountLogRepository accountLogRepository;
    final AccountOAuthRepository accountOAuthRepository;
    final AccountRepository accountRepository;

    public Account getAccount(String accountId) {
        return accountRepository.findAccountById(accountId);
    }

    public Account updateAccount(String accountId, String nickname, String avatar, String background, String color) {
        Account account = accountRepository.findAccountById(accountId);
        if (account == null) return null;

        Account updated = new Account(
                account.accountId(),
                account.created(),
                account.verified(),
                account.username(),
                nickname,
                avatar,
                account.email(),
                background,
                color
        );

        return accountRepository.save(updated);
    }

    public List<AccountAuthorization> getAccountAuthorizationList(String accountId, int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                Math.min(size, MAX_PAGE_SIZE)
        );
        Page<AccountAuthorization> accountAuthorizationPage = accountAuthorizationRepository.getAccountAuthorizationList(accountId, pageable);

        return accountAuthorizationPage.getContent();
    }

    public AccountAuthorization getAccountAuthorization(String accountId, String accountAuthorizationId) {
        return accountAuthorizationRepository.getAccountAuthorizationByCreatedByAndAccountAuthorizationId(accountId, accountAuthorizationId);
    }

    public AccountAuthorization deleteAccountAuthorization(String accountId, String accountAuthorizationId) {
        return null;
    }

    public List<AccountLog> getAccountLogList(String accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE));
        Page<AccountLog> accountLogPage = accountLogRepository.getAccountLogList(accountId, pageable);

        return accountLogPage.getContent();
    }

    public AccountLog getAccountLog(String accountId, String accountLogId) {
        return accountLogRepository.getAccountLogByCreatedByAndAccountLogId(accountId, accountLogId);
    }

    public List<AccountOAuth> getAccountOAuthList(String accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE));
        Page<AccountOAuth> accountOAuthPage = accountOAuthRepository.getAccountOAuthList(accountId, pageable);

        return accountOAuthPage.getContent();
    }

    public AccountOAuth getAccountOAuth(String accountId, String accountOAuthId) {
        return accountOAuthRepository.getAccountOAuthByCreatedByAndAccountOAuthId(accountId, accountOAuthId);
    }

    public AccountOAuth deleteAccountOAuth(String accountId, String accountOAuthId) {
        return accountOAuthRepository.deleteAccountOAuthByCreatedByAndAccountOAuthId(accountId, accountOAuthId);
    }
}
