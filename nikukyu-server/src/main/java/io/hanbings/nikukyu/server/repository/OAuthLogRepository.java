package io.hanbings.nikukyu.server.repository;

import io.hanbings.nikukyu.server.model.OAuthLog;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OAuthLogRepository extends MongoRepository<OAuthLog, String> {
    @Override
    <T extends OAuthLog> @NotNull T save(@NotNull T entity);
}
