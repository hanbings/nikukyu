package io.hanbings.nikukyu.server.service;

import io.hanbings.nikukyu.server.repository.OAuthClientRepository;
import io.hanbings.nikukyu.server.repository.OAuthLogRepository;
import io.hanbings.nikukyu.server.repository.OAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthService {
    final OAuthRepository oauthRepository;
    final OAuthClientRepository oauthClientRepository;
    final OAuthLogRepository oauthLogRepository;
}
