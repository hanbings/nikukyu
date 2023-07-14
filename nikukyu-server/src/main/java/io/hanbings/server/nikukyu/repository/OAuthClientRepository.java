package io.hanbings.server.nikukyu.repository;

import io.hanbings.server.nikukyu.model.OAuthClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OAuthClientRepository extends MongoRepository<OAuthClient, String> {
    @Override
    <S extends OAuthClient> @NotNull S save(@NotNull S entity);

    @SuppressWarnings("SpellCheckingInspection")
    List<OAuthClient> findByOuid(String ouid);

    @SuppressWarnings("SpellCheckingInspection")
    OAuthClient findByOcid(String ocid);
}
