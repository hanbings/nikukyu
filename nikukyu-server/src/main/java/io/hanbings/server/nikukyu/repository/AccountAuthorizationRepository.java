package io.hanbings.server.nikukyu.repository;

import io.hanbings.server.nikukyu.model.AccountAuthorization;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;


public interface AccountAuthorizationRepository extends MongoRepository<AccountAuthorization, UUID> {
    @Override
    <S extends AccountAuthorization> @NotNull S save(@NotNull S entity);

    List<AccountAuthorization> findByOpenid(String openid);

    @SuppressWarnings("SpellCheckingInspection")
    List<AccountAuthorization> findByAuid(UUID auid);
}
