package io.hanbings.nikukyu.server.service;

import io.hanbings.nikukyu.server.model.Account;
import io.hanbings.nikukyu.server.model.AccountAuthorization;
import io.hanbings.nikukyu.server.repository.AccountAuthorizationRepository;
import io.hanbings.nikukyu.server.repository.AccountLogRepository;
import io.hanbings.nikukyu.server.repository.AccountOAuthRepository;
import io.hanbings.nikukyu.server.repository.AccountRepository;
import io.hanbings.nikukyu.server.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    final AccountRepository accountRepository;
    final AccountOAuthRepository accountOAuthRepository;
    final AccountLogRepository accountLogRepository;
    final AccountAuthorizationRepository accountAuthorizationService;

    public Account getAccount(Long id) {
        return accountRepository.findById(id).orElse(null);
    }

    public Account getAccountWithEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public AccountAuthorization getAccountAuthorization(String openid) {
        return accountAuthorizationService.findByOpenid(openid);
    }

    public Account createAccount(
            boolean verified,
            String id,
            String nick,
            String avatar,
            String background,
            String color,
            String email
    ) {
        return accountRepository.save(
                new Account(
                        null,
                        null,
                        TimeUtils.getMilliUnixTime(),
                        verified,
                        id,
                        nick,
                        avatar,
                        background,
                        color,
                        email
                )
        );
    }

    public AccountAuthorization createAccountAuthorization(
            Long id,
            String provider,
            String openid
    ) {

        AccountAuthorization authorization = new AccountAuthorization(
                null,
                null,
                TimeUtils.getMilliUnixTime(),
                id,
                provider,
                openid
        );

        return accountAuthorizationService.save(authorization);
    }
}
