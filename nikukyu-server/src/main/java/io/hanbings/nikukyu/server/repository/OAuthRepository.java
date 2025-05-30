package io.hanbings.nikukyu.server.repository;

import io.hanbings.nikukyu.server.model.OAuth;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OAuthRepository extends JpaRepository<OAuth, Long> {
    OAuth findByUuid(UUID uuid);

    @NotNull Page<OAuth> findAll(@NotNull Pageable pageable);
}
