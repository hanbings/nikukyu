package io.hanbings.server.nikukyu.repository;

import io.hanbings.server.nikukyu.model.AccountLog;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public interface AccountLogRepository extends MongoRepository<AccountLog, String> {
    @Override
    <S extends AccountLog> @NotNull S save(@NotNull S entity);

    List<AccountLog> findByAuid(String auid);

    AccountLog findByAlid(String alid);

}
