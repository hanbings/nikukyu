package io.hanbings.server.nikukyu.repository;

import io.hanbings.server.nikukyu.model.AccountAuthorization;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public interface AccountAuthorizationRepository extends MongoRepository<AccountAuthorization, String> {
    @Override
    <S extends AccountAuthorization> @NotNull S save(@NotNull S entity);

    List<AccountAuthorization> findByOpenid(String openid);

    List<AccountAuthorization> findByAuid(String auid);

    AccountAuthorization findByAaid(String aaid);

    void deleteAccountAuthorizationByAaid(String aaid);
}
