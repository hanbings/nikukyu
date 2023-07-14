package io.hanbings.server.nikukyu.repository;


import io.hanbings.server.nikukyu.model.Account;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

@SuppressWarnings("SpellCheckingInspection")
public interface AccountRepository extends MongoRepository<Account, UUID> {
    @Override
    <S extends Account> @NotNull S save(@NotNull S entity);


    Account findByAuid(String auid);

    Account findByEmail(String email);

    Account findById(String id);

    Account updateAccountByAuid(String auid, Account account);

}
