package io.hanbings.nikukyu.server.repository;

import io.hanbings.nikukyu.server.model.AccountOAuth;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountOAuthRepository extends MongoRepository<AccountOAuth, String> {
    @Override
    <T extends AccountOAuth> @NotNull T save(@NotNull T entity);
}
