package io.hanbings.server.nikukyu.repository;

import io.hanbings.server.nikukyu.model.OAuthLog;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public interface OAuthLogRepository extends MongoRepository<OAuthLog, String> {
    @Override
    <S extends OAuthLog> @NotNull S save(@NotNull S entity);

    List<OAuthLog> findByOuid(String ouid);

}
