package io.hanbings.nikukyu.server.repository;

import io.hanbings.nikukyu.common.model.Account;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountRepository extends MongoRepository<Account, String> {
    @Override
    <T extends Account> @NotNull T save(@NotNull T entity);

    Account findAccountByAccountId(String account);

    Account findAccountByEmail(String email);
}
