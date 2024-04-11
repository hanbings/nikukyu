package io.hanbings.nikukyu.server.repository;

import io.hanbings.nikukyu.server.model.OAuth;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OAuthRepository extends MongoRepository<OAuth, String> {
    @Override
    <T extends OAuth> @NotNull T save(@NotNull T entity);

    Page<OAuth> findAllByCreatedBy(String accountId, Pageable pageable);

    OAuth findOAuthByOauthId(String oauthId);

    OAuth deleteOAuthByOauthId(String oauthId);
}
