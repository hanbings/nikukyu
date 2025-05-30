package io.hanbings.nikukyu.server.repository;

import io.hanbings.nikukyu.server.model.OAuthClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OAuthClientRepository extends JpaRepository<OAuthClient, Long> {
    OAuthClient findByUuid(UUID uuid);

    @NotNull Page<OAuthClient> findAll(@NotNull Pageable pageable);
} 