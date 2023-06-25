package io.hanbings.server.nikukyu.repository;

import io.hanbings.server.nikukyu.data.OAuthLog;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface OAuthLogRepository extends MongoRepository<OAuthLog, UUID> {
    @Override
    <S extends OAuthLog> @NotNull S save(@NotNull S entity);

    @SuppressWarnings("SpellCheckingInspection")
    List<OAuthLog> findByOuid(UUID ouid);
}
