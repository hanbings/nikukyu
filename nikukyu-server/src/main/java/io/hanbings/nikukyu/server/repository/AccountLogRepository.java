package io.hanbings.nikukyu.server.repository;

import io.hanbings.nikukyu.server.model.AccountLog;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountLogRepository extends JpaRepository<AccountLog, Long> {
    AccountLog findByUuid(UUID uuid);

    @NotNull Page<AccountLog> findAll(@NotNull Pageable pageable);
} 