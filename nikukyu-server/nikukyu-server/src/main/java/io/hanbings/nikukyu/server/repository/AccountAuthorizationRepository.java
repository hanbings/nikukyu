package io.hanbings.nikukyu.server.repository;

import io.hanbings.nikukyu.common.model.AccountAuthorization;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface AccountAuthorizationRepository extends MongoRepository<AccountAuthorization, String> {
    @Override
    <T extends AccountAuthorization> @NotNull T save(@NotNull T entity);

    @Query("{created_by: ?0}")
    Page<AccountAuthorization> getAccountAuthorizationList(String createdBy, Pageable pageable);

    AccountAuthorization getAccountAuthorizationByOpenid(String openid);

    AccountAuthorization getAccountAuthorizationByCreatedByAndAccountAuthorizationId(String createdBy, String accountAuthorizationId);

    AccountAuthorization deleteAccountAuthorizationByAccountAuthorizationId(String accountAuthorizationId);
}
