package io.hanbings.nikukyu.server.repository;

import io.hanbings.nikukyu.server.model.AccountOAuth;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface AccountOAuthRepository extends MongoRepository<AccountOAuth, String> {
    @Override
    <T extends AccountOAuth> @NotNull T save(@NotNull T entity);

    @Query("{created_by: ?0}")
    Page<AccountOAuth> getAccountOAuthList(String createdBy, Pageable pageable);

    AccountOAuth getAccountOAuthByCreatedByAndAccountOAuthId(String createdBy, String accountOAuthId);

    AccountOAuth deleteAccountOAuthByCreatedByAndAccountOAuthId(String createdBy, String accountOAuthId);
}
