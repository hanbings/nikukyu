package io.hanbings.nikukyu.server.repository;

import io.hanbings.nikukyu.common.model.OAuthClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OAuthClientRepository extends MongoRepository<OAuthClient, String> {
    @Override
    <T extends OAuthClient> @NotNull T save(@NotNull T entity);

    Page<OAuthClient> findAllByCreatedBy(String accountId, Pageable pageable);

    OAuthClient findOAuthClientByOauthClientId(String oauthClientId);

    OAuthClient findOAuthClientByCreatedByAndOauthClientId(String accountId, String oauthClientId);

    OAuthClient deleteOAuthClientByCreatedByAndOauthClientId(String accountId, String oauthClientId);
}
