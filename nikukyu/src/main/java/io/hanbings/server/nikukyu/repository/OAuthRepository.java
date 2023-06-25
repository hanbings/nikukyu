package io.hanbings.server.nikukyu.repository;

import io.hanbings.server.nikukyu.data.OAuth;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface OAuthRepository extends MongoRepository<OAuth, UUID> {
    @Override
    <S extends OAuth> @NotNull S save(@NotNull S entity);

    @SuppressWarnings("SpellCheckingInspection")
    OAuth findByOuid(UUID ouid);

    @SuppressWarnings("SpellCheckingInspection")
    List<OAuth> findByAuid(UUID auid);
}