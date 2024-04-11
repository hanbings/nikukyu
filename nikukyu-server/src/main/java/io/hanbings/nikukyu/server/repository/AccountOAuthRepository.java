package io.hanbings.nikukyu.server.repository;

import io.hanbings.nikukyu.server.model.AccountOAuth;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountOAuthRepository extends MongoRepository<AccountOAuth, String> {
    @Override
    <T extends AccountOAuth> @NotNull T save(@NotNull T entity);

    Page<AccountOAuth> getAccountOAuthList(String accountId, Pageable pageable);

    AccountOAuth getAccountOAuthByCreatedByAndAccountOAuthId(String accountId, String accountOAuthId);

    AccountOAuth deleteAccountOAuthByCreatedByAndAccountOAuthId(String accountId, String accountOAuthId);
}
