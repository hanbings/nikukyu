package io.hanbings.nikukyu.server.repository;

import io.hanbings.nikukyu.server.model.AccountAuthorization;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountAuthorizationRepository extends MongoRepository<AccountAuthorization, String> {
    @Override
    <T extends AccountAuthorization> @NotNull T save(@NotNull T entity);
}
