package io.hanbings.server.nikukyu.repository;

import io.hanbings.server.nikukyu.model.OAuth;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public interface OAuthRepository extends MongoRepository<OAuth, String> {
    @Override
    <S extends OAuth> @NotNull S save(@NotNull S entity);

    OAuth findByOuid(String ouid);

    List<OAuth> findByAuid(String auid);

    OAuth updateOAuthByOuid(String ouid, OAuth oAuth);

    void deleteOAuthByOuid(String ouid);
}