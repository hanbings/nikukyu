package io.hanbings.server.nikukyu.repository;

import io.hanbings.server.nikukyu.data.OAuthLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface OAuthLogRepository extends MongoRepository<OAuthLog, UUID> {
}
