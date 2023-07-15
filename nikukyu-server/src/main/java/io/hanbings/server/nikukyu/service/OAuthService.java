package io.hanbings.server.nikukyu.service;


import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.content.OAuthLogType;
import io.hanbings.server.nikukyu.model.OAuth;
import io.hanbings.server.nikukyu.model.OAuthClient;
import io.hanbings.server.nikukyu.model.OAuthLog;
import io.hanbings.server.nikukyu.repository.OAuthClientRepository;
import io.hanbings.server.nikukyu.repository.OAuthLogRepository;
import io.hanbings.server.nikukyu.repository.OAuthRepository;
import io.hanbings.server.nikukyu.utils.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@SuppressWarnings("SpellCheckingInspection")
public class OAuthService {
    final OAuthRepository oAuthRepository;
    final OAuthLogRepository oAuthLogRepository;
    final OAuthClientRepository oAuthClientRepository;

    // OAuth Client
    public OAuthClient createOAuthClient(String ouid, long expire) {
        OAuthClient client = new OAuthClient(
                RandomUtils.uuid(),
                System.currentTimeMillis(),
                ouid,
                RandomUtils.strings(32),
                expire
        );

        return oAuthClientRepository.save(client);
    }

    public OAuthClient updateOAuthClientWithOcid(String ocid, long expire) {
        OAuthClient old = oAuthClientRepository.findByOcid(ocid);
        OAuthClient client = new OAuthClient(
                old.ocid(),
                old.created(),
                old.ouid(),
                old.secret(),
                expire == 0 ? old.expire() : expire
        );

        return oAuthClientRepository.updateOAuthClientByOcid(ocid, client);
    }

    public List<OAuthClient> getOAuthClientsWithOuid(String ouid) {
        List<OAuthClient> datas = oAuthClientRepository.findByOuid(ouid);
        List<OAuthClient> clients = new ArrayList<>();

        datas.forEach(data ->
                clients.add(
                        new OAuthClient(
                                data.ocid(),
                                data.created(),
                                data.ouid(),
                                data.secret().substring(0, 4) + "****************************",
                                data.expire()
                        )
                )
        );

        return clients.isEmpty() ? null : clients;
    }

    public OAuthClient getOAuthClientWithOcid(String ocid) {
        OAuthClient client = oAuthClientRepository.findByOcid(ocid);

        return new OAuthClient(
                client.ocid(),
                client.created(),
                client.ouid(),
                client.secret().substring(0, 4) + "****************************",
                client.expire()
        );
    }

    public boolean checkSecretWithOcid(String ocid, String secret) {
        OAuthClient client = oAuthClientRepository.findByOcid(ocid);
        if (client == null) return false;

        return Objects.equals(client.secret(), secret);
    }

    public void deleteOAuthClientWithOcid(String ocid) {
        oAuthClientRepository.deleteOAuthClientByOcid(ocid);
    }

    // OAuth Log
    public OAuthLog createOAuthLog(String ouid, String ip, OAuthLogType type) {
        OAuthLog log = new OAuthLog(
                RandomUtils.uuid(),
                System.currentTimeMillis(),
                ouid,
                ip,
                type
        );

        return oAuthLogRepository.save(log);
    }

    public List<OAuthLog> getOAuthLogsWithOuid(String ouid) {
        return oAuthLogRepository.findByOuid(ouid);
    }

    public OAuthLog getOAuthLogWithOlid(String olid) {
        return oAuthLogRepository.findById(olid).orElse(null);
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

    public OAuth updateOAuthWithOuid(
            String ouid,
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
        OAuth old = oAuthRepository.findByAuid(ouid).get(0);
        OAuth oAuth = new OAuth(
                old.ouid(),
                old.created(),
                old.auid(),
                redirect == null ? old.redirect() : redirect,
                access == null ? old.access() : access,
                avatar == null ? old.avatar() : avatar,
                name == null ? old.name() : name,
                description == null ? old.description() : description,
                homepage == null ? old.homepage() : homepage,
                background == null ? old.background() : background,
                theme == null ? old.theme() : theme,
                policy == null ? old.policy() : policy,
                tos == null ? old.tos() : tos
        );

        return oAuthRepository.updateOAuthByOuid(old.ouid(), oAuth);
    }

    public OAuth getOAuthWithOuid(String ouid) {
        return oAuthRepository.findByOuid(ouid);
    }

    public List<OAuth> getOAuthWithAuid(String auid) {
        return oAuthRepository.findByAuid(auid);
    }

    public void deleteOAuthWithOuid(String ouid) {
        oAuthClientRepository.deleteOAuthClientsByOuid(ouid);
        oAuthRepository.deleteOAuthByOuid(ouid);
    }
}
