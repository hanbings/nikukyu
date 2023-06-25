package io.hanbings.server.nikukyu.repository;

import io.hanbings.server.nikukyu.data.AccountLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface AccountLogRepository extends MongoRepository<AccountLog, UUID> {
}
