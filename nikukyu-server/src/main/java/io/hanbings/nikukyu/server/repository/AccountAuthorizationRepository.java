package io.hanbings.nikukyu.server.repository;

import io.hanbings.nikukyu.server.model.AccountAuthorization;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountAuthorizationRepository extends JpaRepository<AccountAuthorization, Long> {
    AccountAuthorization findByUuid(UUID uuid);

    @NotNull Page<AccountAuthorization> findAll(@NotNull Pageable pageable);

    AccountAuthorization findByOpenid(String openid);
}