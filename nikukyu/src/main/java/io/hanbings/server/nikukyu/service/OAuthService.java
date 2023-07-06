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
    final OAuthClientRepository clients;
    final OAuthLogRepository logs;
    final OAuthRepository oauths;

    // OAuth Client
    public OAuthClient createOAuthClient(OAuthClient client) {
        return clients.save(client);
    }

    public OAuthClient getOAuthClientWithOuid(UUID ouid) {
        return clients.findByOuid(ouid).get(0);
    }

    public List<OAuthClient> getOAuthClientsWithOuid(UUID ouid) {
        return clients.findByOuid(ouid);
    }

    public OAuthClient getOAuthClientWithOcid(UUID ocid) {
        return clients.findByOcid(ocid);
    }

    // OAuth Log
    public OAuthLog createOAuthLog(OAuthLog log) {
        return logs.save(log);
    }

    public List<OAuthLog> getOAuthLogsWithOuid(UUID ouid) {
        return logs.findByOuid(ouid);
    }

    // OAuth
    public OAuth createOAuth(OAuth oauth) {
        return oauths.save(oauth);
    }

    public OAuth getOAuthWithOuid(UUID ouid) {
        return oauths.findByOuid(ouid);
    }

    public List<OAuth> getOAuthWithAuid(UUID auid) {
        return oauths.findByAuid(auid);
    }
}
