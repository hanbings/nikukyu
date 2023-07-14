package io.hanbings.server.nikukyu.repository;

import io.hanbings.server.nikukyu.model.OAuth;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OAuthRepository extends MongoRepository<OAuth, String> {
    @Override
    <S extends OAuth> @NotNull S save(@NotNull S entity);

    @SuppressWarnings("SpellCheckingInspection")
    OAuth findByOuid(String ouid);

    @SuppressWarnings("SpellCheckingInspection")
    List<OAuth> findByAuid(String auid);
}