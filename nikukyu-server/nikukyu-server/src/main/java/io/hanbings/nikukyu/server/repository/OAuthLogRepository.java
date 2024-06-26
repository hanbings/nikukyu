package io.hanbings.nikukyu.server.repository;

import io.hanbings.nikukyu.common.model.OAuthLog;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OAuthLogRepository extends MongoRepository<OAuthLog, String> {
    @Override
    <T extends OAuthLog> @NotNull T save(@NotNull T entity);

    Page<OAuthLog> findAllByCreatedBy(String createdBy, Pageable pageable);

    OAuthLog deleteOAuthLogByCreatedByAndOauthLogId(String createdBy, String oauthLogId);
}
