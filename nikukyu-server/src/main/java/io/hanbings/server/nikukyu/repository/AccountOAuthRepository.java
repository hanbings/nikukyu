package io.hanbings.server.nikukyu.repository;

import io.hanbings.server.nikukyu.model.AccountAuthorization;
import io.hanbings.server.nikukyu.model.AccountOAuth;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AccountOAuthRepository extends MongoRepository<AccountOAuth, String> {
    @Override
    <S extends AccountOAuth> @NotNull S save(@NotNull S entity);

    @SuppressWarnings("SpellCheckingInspection")
    List<AccountAuthorization> findByAuid(String auid);
}
