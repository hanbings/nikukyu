package io.hanbings.nikukyu.server.service;

import io.hanbings.nikukyu.server.model.OAuth;
import io.hanbings.nikukyu.server.model.OAuthClient;
import io.hanbings.nikukyu.server.model.OAuthLog;
import io.hanbings.nikukyu.server.repository.OAuthClientRepository;
import io.hanbings.nikukyu.server.repository.OAuthLogRepository;
import io.hanbings.nikukyu.server.repository.OAuthRepository;
import io.hanbings.nikukyu.server.utils.RandomUtils;
import io.hanbings.nikukyu.server.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OAuthService {
    static int MAX_PAGE_SIZE = 20;
    final OAuthClientRepository oAuthClientRepository;
    final OAuthLogRepository oAuthLogRepository;
    final OAuthRepository oAuthRepository;

    public List<OAuth> getOAuthList(String accountId, int page, int size) {
        Pageable pageable = Pageable.ofSize(Math.min(size, MAX_PAGE_SIZE));
        Page<OAuth> oAuthPage = oAuthRepository.findAllByCreatedBy(accountId, pageable);

        return oAuthPage.getContent();
    }

    public OAuth createOAuth(
            String accountId, String name,
            Set<String> redirect, Set<String> access,
            String avatar, String description, String homepage,
            String background, String theme,
            String policy, String tos
    ) {
        OAuth oAuth = new OAuth(
                RandomUtils.uuid(),
                TimeUtils.getMilliUnixTime(),
                accountId,
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
        );

        return oAuthRepository.save(oAuth);
    }

    public OAuth getOAuth(String oauthId) {
        return oAuthRepository.findOAuthByOauthId(oauthId);
    }

    public OAuth updateOAuth(
            String oauthId,
            String name,
            Set<String> redirect, Set<String> access,
            String avatar, String description, String homepage,
            String background, String theme,
            String policy, String tos
    ) {
        OAuth oAuth = oAuthRepository.findOAuthByOauthId(oauthId);
        if (oAuth == null) return null;

        OAuth updated = new OAuth(
                oauthId,
                oAuth.created(),
                oAuth.createdBy(),
                redirect != null ? redirect : oAuth.redirect(),
                access != null ? access : oAuth.access(),
                avatar != null ? avatar : oAuth.avatar(),
                name != null ? name : oAuth.name(),
                description != null ? description : oAuth.description(),
                homepage != null ? homepage : oAuth.homepage(),
                background != null ? background : oAuth.background(),
                theme != null ? theme : oAuth.theme(),
                policy != null ? policy : oAuth.policy(),
                tos != null ? tos : oAuth.tos()
        );

        return oAuthRepository.save(updated);
    }

    public OAuth deleteOAuth(String oauthId) {
        return oAuthRepository.deleteOAuthByOauthId(oauthId);
    }

    @SuppressWarnings("all")
    public List<OAuthClient> getOAuthClientList(String oauthId, int page, int size) {
        Pageable pageable = Pageable.ofSize(Math.min(size, MAX_PAGE_SIZE));
        Page<OAuthClient> oAuthClientPage = oAuthClientRepository.findAllByCreatedBy(oauthId, pageable);

        List<OAuthClient> oAuthClientList = oAuthClientPage.getContent();

        return oAuthClientList
                .stream()
                .map(oAuthClient -> new OAuthClient(
                        oAuthClient.oauthClientId(),
                        oAuthClient.created(),
                        oAuthClient.createdBy(),
                        String.format("%s-******-******-******-******-******", oAuthClient.secret().substring(0, 6)),
                        oAuthClient.expire()
                )).toList();
    }

    public OAuthClient createOAuthClient(String oauthId, long expire) {
        OAuthClient oAuthClient = new OAuthClient(
                RandomUtils.uuid(),
                TimeUtils.getMilliUnixTime(),
                oauthId,
                RandomUtils.uuid(),
                expire > TimeUtils.getMilliUnixTime() ? expire : 0
        );

        return oAuthClientRepository.save(oAuthClient);
    }

    @SuppressWarnings("all")
    public OAuthClient getOAuthClient(String oauthClientId) {
        OAuthClient oAuthClient = oAuthClientRepository.findOAuthClientByOauthClientId(oauthClientId);

        return new OAuthClient(
                oAuthClient.oauthClientId(),
                oAuthClient.created(),
                oAuthClient.createdBy(),
                String.format("%s-******-******-******-******-******", oAuthClient.secret().substring(0, 6)),
                oAuthClient.expire()
        );
    }

    @SuppressWarnings("all")
    public OAuthClient getOAuthClient(String oauthId, String oauthClientId) {
        OAuthClient oAuthClient = oAuthClientRepository.findOAuthClientByCreatedByAndOauthClientId(oauthId, oauthClientId);

        return new OAuthClient(
                oAuthClient.oauthClientId(),
                oAuthClient.created(),
                oAuthClient.createdBy(),
                String.format("%s-******-******-******-******-******", oAuthClient.secret().substring(0, 6)),
                oAuthClient.expire()
        );
    }

    public OAuthClient deleteOAuthClient(String oauthId, String oauthClientId) {
        return oAuthClientRepository.deleteOAuthClientByCreatedByAndOauthClientId(oauthId, oauthClientId);
    }

    public List<OAuthLog> getOAuthLogList(String oauthId, int page, int size) {
        Pageable pageable = Pageable.ofSize(Math.min(size, MAX_PAGE_SIZE));
        Page<OAuthLog> oAuthLogPage = oAuthLogRepository.findAllByCreatedBy(oauthId, pageable);

        return oAuthLogPage.getContent();
    }

    public OAuthLog getOAuthLog(String oauthId, String oauthLogId) {
        return oAuthLogRepository.deleteOAuthLogByCreatedByAndOauthLogId(oauthId, oauthLogId);
    }

    public boolean checkSecret(String clientId, String secret) {
        OAuthClient client = oAuthClientRepository.findOAuthClientByOauthClientId(clientId);
        if (client == null) return false;

        return Objects.equals(client.secret(), secret);
    }
}
