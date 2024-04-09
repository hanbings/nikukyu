package io.hanbings.nikukyu.server.repository;

import io.hanbings.nikukyu.server.model.OAuthClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OAuthClientRepository extends MongoRepository<OAuthClient, String> {
    @Override
    <T extends OAuthClient> @NotNull T save(@NotNull T entity);
}
