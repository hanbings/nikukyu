package io.hanbings.nikukyu.server.repository;

import io.hanbings.nikukyu.server.model.Account;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByUuid(UUID uuid);

    @NotNull Page<Account> findAll(@NotNull Pageable pageable);

    Account findByEmail(String email);
}
