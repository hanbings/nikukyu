package io.hanbings.nikukyu.server.repository;

import io.hanbings.nikukyu.server.model.OAuthLog;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OAuthLogRepository extends JpaRepository<OAuthLog, Long> {
    OAuthLog findByUuid(UUID uuid);

    @NotNull Page<OAuthLog> findAll(@NotNull Pageable pageable);
} 