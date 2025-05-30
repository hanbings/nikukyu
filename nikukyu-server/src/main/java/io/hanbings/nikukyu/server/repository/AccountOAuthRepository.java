package io.hanbings.nikukyu.server.repository;

import io.hanbings.nikukyu.server.model.AccountOAuth;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountOAuthRepository extends JpaRepository<AccountOAuth, Long> {
    AccountOAuth findByUuid(UUID uuid);

    @NotNull Page<AccountOAuth> findAll(@NotNull Pageable pageable);
} 