package io.hanbings.server.nikukyu.repository;

import io.hanbings.server.nikukyu.data.OAuthClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface OAuthClientRepository extends MongoRepository<OAuthClient, UUID> {
    @Override
    <S extends OAuthClient> @NotNull S save(@NotNull S entity);

    @SuppressWarnings("SpellCheckingInspection")
    List<OAuthClient> findByOuid(UUID ouid);

    @SuppressWarnings("SpellCheckingInspection")
    OAuthClient findByOcid(UUID ocid);
}
