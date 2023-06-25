package io.hanbings.server.nikukyu.repository;

import io.hanbings.server.nikukyu.data.AccountOAuth;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface AccountOAuthRepository extends MongoRepository<AccountOAuth, UUID> {
}
