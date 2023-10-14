package io.hanbings.server.nikukyu.repository;

import io.hanbings.server.nikukyu.model.OAuthClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public interface OAuthClientRepository extends MongoRepository<OAuthClient, String> {
    @Override
    <S extends OAuthClient> @NotNull S save(@NotNull S entity);

    List<OAuthClient> findByOuid(String ouid);

    OAuthClient findByOcid(String ocid);

    @Query("{'ocid': ?0}")
    OAuthClient updateOAuthClientByOcid(String ocid, OAuthClient oAuthClient);

    void deleteOAuthClientByOcid(String ocid);

    void deleteOAuthClientsByOuid(String ouid);
}
