package io.hanbings.nikukyu.server.repository;

import io.hanbings.nikukyu.server.model.AccountLog;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountLogRepository extends MongoRepository<AccountLog, String> {
    @Override
    <T extends AccountLog> @NotNull T save(@NotNull T entity);

    Page<AccountLog> getAccountLogList(String accountId, Pageable pageable);

    AccountLog getAccountLogByCreatedByAndAccountLogId(String accountId, String accountLogId);
}
