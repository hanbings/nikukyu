package io.hanbings.nikukyu.server.controller;

import io.hanbings.nikukyu.server.annotation.NikukyuPermissionCheck;
import io.hanbings.nikukyu.server.data.OAuthClientDto;
import io.hanbings.nikukyu.server.data.OAuthDto;
import io.hanbings.nikukyu.server.exception.NotFoundException;
import io.hanbings.nikukyu.server.model.OAuth;
import io.hanbings.nikukyu.server.security.Header;
import io.hanbings.nikukyu.server.service.OAuthService;
import io.hanbings.nikukyu.server.utils.RandomUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/oauth")
public class OAuthController {
    final HttpServletRequest request;
    final OAuthService oAuthService;

    @GetMapping()
    @NikukyuPermissionCheck(access = {"oauth"})
    public Object getOAuthList(
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {
        String accountId = request.getHeader(Header.ACCOUNT);
        if (accountId == null) throw new NotFoundException(RandomUtils.uuid(), Header.ACCOUNT, request.getRequestURI());

        return oAuthService.getOAuthList(accountId, page, size);
    }

    @PostMapping
    public Object createOAuth(@RequestBody OAuthDto dto) {
        String accountId = request.getHeader(Header.ACCOUNT);
        if (accountId == null) throw new NotFoundException(RandomUtils.uuid(), Header.ACCOUNT, request.getRequestURI());

        return oAuthService.createOAuth(
                accountId,
                dto.name() != null ? dto.name() : "New OAuth Application",
                dto.redirect() != null ? dto.redirect() : Set.of(),
                dto.access() != null ? dto.access() : Set.of(),
                dto.avatar() != null ? dto.avatar() : null,
                dto.description() != null ? dto.description() : null,
                dto.homepage() != null ? dto.homepage() : null,
                dto.background() != null ? dto.background() : null,
                dto.theme() != null ? dto.theme() : null,
                dto.policy() != null ? dto.policy() : null,
                dto.tos() != null ? dto.tos() : null
        );
    }

    @GetMapping("{oauth_id}")
    public Object getOAuth(@PathVariable("oauth_id") String oauthId) {
        @SuppressWarnings("all")
        OAuth oAuth = oAuthService.getOAuth(oauthId);
        if (oAuth == null) throw new NotFoundException(RandomUtils.uuid(), oauthId, request.getRequestURI());

        String accountToken = request.getHeader(Header.ACCOUNT);
        if (!Objects.equals(accountToken, oAuth.createdBy()))
            throw new NotFoundException(RandomUtils.uuid(), oauthId, request.getRequestURI());

        return oAuth;
    }

    @PostMapping("{oauth_id}")
    public Object updateOAuth(@PathVariable("oauth_id") String oauthId, @RequestBody OAuthDto dto) {
        @SuppressWarnings("all")
        OAuth oAuth = oAuthService.getOAuth(oauthId);
        if (oAuth == null) throw new NotFoundException(RandomUtils.uuid(), oauthId, request.getRequestURI());

        String accountToken = request.getHeader(Header.ACCOUNT);
        if (!Objects.equals(accountToken, oAuth.createdBy()))
            throw new NotFoundException(RandomUtils.uuid(), oauthId, request.getRequestURI());

        OAuth updated = oAuthService.updateOAuth(
                oauthId,
                dto.name(),
                dto.redirect(),
                dto.access(),
                dto.avatar(),
                dto.description(),
                dto.homepage(),
                dto.background(),
                dto.theme(),
                dto.policy(),
                dto.tos()
        );

        if (updated == null) throw new NotFoundException(RandomUtils.uuid(), oauthId, request.getRequestURI());

        return updated;
    }

    @DeleteMapping("{oauth_id}")
    public Object deleteOAuth(@PathVariable("oauth_id") String oauthId) {
        @SuppressWarnings("all")
        OAuth oAuth = oAuthService.getOAuth(oauthId);
        if (oAuth == null) throw new NotFoundException(RandomUtils.uuid(), oauthId, request.getRequestURI());

        String accountToken = request.getHeader(Header.ACCOUNT);
        if (!Objects.equals(accountToken, oAuth.createdBy()))
            throw new NotFoundException(RandomUtils.uuid(), oauthId, request.getRequestURI());

        return oAuthService.deleteOAuth(oauthId);
    }

    @GetMapping("{oauth_id}/client")
    public Object getClientList(
            @PathVariable("oauth_id") String oauthId,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {
        @SuppressWarnings("all")
        OAuth oAuth = oAuthService.getOAuth(oauthId);
        if (oAuth == null) throw new NotFoundException(RandomUtils.uuid(), oauthId, request.getRequestURI());

        String accountToken = request.getHeader(Header.ACCOUNT);
        if (!Objects.equals(accountToken, oAuth.createdBy()))
            throw new NotFoundException(RandomUtils.uuid(), oauthId, request.getRequestURI());

        return oAuthService.getOAuthClientList(oauthId, page, size);
    }

    @PostMapping("{oauth_id}/client")
    public Object createClient(@PathVariable("oauth_id") String oauthId, @RequestBody OAuthClientDto dto) {
        @SuppressWarnings("all")
        OAuth oAuth = oAuthService.getOAuth(oauthId);
        if (oAuth == null) throw new NotFoundException(RandomUtils.uuid(), oauthId, request.getRequestURI());

        String accountToken = request.getHeader(Header.ACCOUNT);
        if (!Objects.equals(accountToken, oAuth.createdBy()))
            throw new NotFoundException(RandomUtils.uuid(), oauthId, request.getRequestURI());

        return oAuthService.createOAuthClient(oauthId, dto.expire());
    }

    @GetMapping("{oauth_id}/client/{client_id}")
    public Object getClient(
            @PathVariable("oauth_id") String oauthId,
            @PathVariable("client_id") String clientId
    ) {

        @SuppressWarnings("all")
        OAuth oAuth = oAuthService.getOAuth(oauthId);
        if (oAuth == null) throw new NotFoundException(RandomUtils.uuid(), oauthId, request.getRequestURI());

        String accountToken = request.getHeader(Header.ACCOUNT);
        if (!Objects.equals(accountToken, oAuth.createdBy()))
            throw new NotFoundException(RandomUtils.uuid(), oauthId, request.getRequestURI());

        return oAuthService.getOAuthClient(oauthId, clientId);
    }

    @DeleteMapping("{oauth_id}/client/{client_id}")
    public Object deleteClient(
            @PathVariable("oauth_id") String oauthId,
            @PathVariable("client_id") String clientId
    ) {
        @SuppressWarnings("all")
        OAuth oAuth = oAuthService.getOAuth(oauthId);
        if (oAuth == null) throw new NotFoundException(RandomUtils.uuid(), oauthId, request.getRequestURI());

        String accountToken = request.getHeader(Header.ACCOUNT);
        if (!Objects.equals(accountToken, oAuth.createdBy()))
            throw new NotFoundException(RandomUtils.uuid(), oauthId, request.getRequestURI());

        return oAuthService.deleteOAuthClient(oauthId, clientId);
    }

    @GetMapping("{oauth_id}/log")
    public Object getLogList(
            @PathVariable("oauth_id") String oauthId,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {
        @SuppressWarnings("all")
        OAuth oAuth = oAuthService.getOAuth(oauthId);
        if (oAuth == null) throw new NotFoundException(RandomUtils.uuid(), oauthId, request.getRequestURI());

        String accountToken = request.getHeader(Header.ACCOUNT);
        if (!Objects.equals(accountToken, oAuth.createdBy()))
            throw new NotFoundException(RandomUtils.uuid(), oauthId, request.getRequestURI());

        return oAuthService.getOAuthLogList(oauthId, page, size);
    }

    @GetMapping("{oauth_id}/log/{log_id}")
    public Object getLog(
            @PathVariable("oauth_id") String oauthId,
            @PathVariable("log_id") String logId
    ) {
        @SuppressWarnings("all")
        OAuth oAuth = oAuthService.getOAuth(oauthId);
        if (oAuth == null) throw new NotFoundException(RandomUtils.uuid(), oauthId, request.getRequestURI());

        String accountToken = request.getHeader(Header.ACCOUNT);
        if (!Objects.equals(accountToken, oAuth.createdBy()))
            throw new NotFoundException(RandomUtils.uuid(), oauthId, request.getRequestURI());

        return oAuthService.getOAuthLog(oauthId, logId);
    }
}
