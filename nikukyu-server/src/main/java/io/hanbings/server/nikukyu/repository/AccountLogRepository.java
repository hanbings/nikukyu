package io.hanbings.server.nikukyu.repository;

import io.hanbings.server.nikukyu.model.AccountLog;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface AccountLogRepository extends MongoRepository<AccountLog, UUID> {
    @Override
    <S extends AccountLog> @NotNull S save(@NotNull S entity);

    @SuppressWarnings("SpellCheckingInspection")
    List<AccountLog> findByAuid(UUID auid);
}
