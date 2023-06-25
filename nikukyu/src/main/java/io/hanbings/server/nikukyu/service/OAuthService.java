package io.hanbings.server.nikukyu.service;


import io.hanbings.server.nikukyu.data.OAuth;
import io.hanbings.server.nikukyu.data.OAuthLog;
import io.hanbings.server.nikukyu.repository.OAuthLogRepository;
import io.hanbings.server.nikukyu.repository.OAuthRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@SuppressWarnings("SpellCheckingInspection")
public class OAuthService {
    OAuthRepository oauths;
    OAuthLogRepository logs;

    public List<OAuth> getOAuthWithAuid(UUID auid) {
        return oauths.findByAuid(auid);
    }

    public List<OAuthLog> getOAuthLogsWithOuid(UUID ouid) {
        return logs.findByOuid(ouid);
    }
}
