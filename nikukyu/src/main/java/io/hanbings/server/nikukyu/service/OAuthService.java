package io.hanbings.server.nikukyu.service;


import io.hanbings.server.nikukyu.model.OAuth;
import io.hanbings.server.nikukyu.model.OAuthClient;
import io.hanbings.server.nikukyu.model.OAuthLog;
import io.hanbings.server.nikukyu.repository.OAuthClientRepository;
import io.hanbings.server.nikukyu.repository.OAuthLogRepository;
import io.hanbings.server.nikukyu.repository.OAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("SpellCheckingInspection")
public class OAuthService {
    final OAuthClientRepository oAuthClientRepository;
    final OAuthLogRepository oAuthLogRepository;
    final OAuthRepository oAuthRepository;

    // OAuth Client
    public OAuthClient createOAuthClient(OAuthClient client) {
        return oAuthClientRepository.save(client);
    }

    public OAuthClient getOAuthClientWithOuid(UUID ouid) {
        return oAuthClientRepository.findByOuid(ouid).get(0);
    }

    public List<OAuthClient> getOAuthClientsWithOuid(UUID ouid) {
        return oAuthClientRepository.findByOuid(ouid);
    }

    public OAuthClient getOAuthClientWithOcid(UUID ocid) {
        return oAuthClientRepository.findByOcid(ocid);
    }

    // OAuth Log
    public OAuthLog createOAuthLog(OAuthLog log) {
        return oAuthLogRepository.save(log);
    }

    public List<OAuthLog> getOAuthLogsWithOuid(UUID ouid) {
        return oAuthLogRepository.findByOuid(ouid);
    }

    // OAuth
    public OAuth createOAuth(OAuth oauth) {
        return oAuthRepository.save(oauth);
    }

    public OAuth getOAuthWithOuid(UUID ouid) {
        return oAuthRepository.findByOuid(ouid);
    }

    public List<OAuth> getOAuthWithAuid(UUID auid) {
        return oAuthRepository.findByAuid(auid);
    }
}
