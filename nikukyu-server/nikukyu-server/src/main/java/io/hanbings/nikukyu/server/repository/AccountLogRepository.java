package io.hanbings.nikukyu.server.repository;

import io.hanbings.nikukyu.common.model.AccountLog;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface AccountLogRepository extends MongoRepository<AccountLog, String> {
    @Override
    <T extends AccountLog> @NotNull T save(@NotNull T entity);

    @Query("{created_by: ?0}")
    Page<AccountLog> getAccountLogList(String createdBy, Pageable pageable);

    AccountLog getAccountLogByCreatedByAndAccountLogId(String createdBy, String accountLogId);
}
