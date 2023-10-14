package io.hanbings.server.nikukyu.repository;

import io.hanbings.server.nikukyu.model.AccountOAuth;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public interface AccountOAuthRepository extends MongoRepository<AccountOAuth, String> {
    @Override
    <S extends AccountOAuth> @NotNull S save(@NotNull S entity);

    List<AccountOAuth> findByAuid(String auid);

    AccountOAuth findByAoid(String aoid);

    void deleteAccountOAuthByAoid(String aoid);
}
