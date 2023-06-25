package io.hanbings.server.nikukyu.repository;

import io.hanbings.server.nikukyu.data.OAuthClient;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface OAuthClientRepository extends MongoRepository<OAuthClient, UUID> {
}
