package io.hanbings.server.nikukyu.service;


import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.model.OAuth;
import io.hanbings.server.nikukyu.model.OAuthClient;
import io.hanbings.server.nikukyu.model.OAuthLog;
import io.hanbings.server.nikukyu.repository.OAuthClientRepository;
import io.hanbings.server.nikukyu.repository.OAuthLogRepository;
import io.hanbings.server.nikukyu.repository.OAuthRepository;
import io.hanbings.server.nikukyu.utils.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("SpellCheckingInspection")
public class OAuthService {
    final OAuthRepository oAuthRepository;
    final OAuthLogRepository oAuthLogRepository;
    final OAuthClientRepository oAuthClientRepository;

    // OAuth Client
    public OAuthClient createOAuthClient(OAuthClient client) {
        return oAuthClientRepository.save(client);
    }

    public OAuthClient getOAuthClientWithOuid(String ouid) {
        return oAuthClientRepository.findByOuid(ouid).get(0);
    }

    public List<OAuthClient> getOAuthClientsWithOuid(String ouid) {
        return oAuthClientRepository.findByOuid(ouid);
    }

    public OAuthClient getOAuthClientWithOcid(String ocid) {
        return oAuthClientRepository.findByOcid(ocid);
    }

    // OAuth Log
    public OAuthLog createOAuthLog(OAuthLog log) {
        return oAuthLogRepository.save(log);
    }

    public List<OAuthLog> getOAuthLogsWithOuid(String ouid) {
        return oAuthLogRepository.findByOuid(ouid);
    }

    // OAuth
    public OAuth createOAuth(
            String auid,
            List<String> redirect,
            List<AccessType> access,
            String avatar,
            String name,
            String description,
            String homepage,
            String background,
            String theme,
            String policy,
            String tos
    ) {
        return oAuthRepository.save(new OAuth(
                RandomUtils.uuid(),
                System.currentTimeMillis(),
                auid,
                redirect,
                access,
                avatar,
                name,
                description,
                homepage,
                background,
                theme,
                policy,
                tos
        ));
    }

    public OAuth getOAuthWithOuid(String ouid) {
        return oAuthRepository.findByOuid(ouid);
    }

    public List<OAuth> getOAuthWithAuid(String auid) {
        return oAuthRepository.findByAuid(auid);
    }
}
