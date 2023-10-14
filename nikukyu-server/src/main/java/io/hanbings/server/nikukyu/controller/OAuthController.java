package io.hanbings.server.nikukyu.controller;

import io.hanbings.server.nikukyu.annotation.NikukyuTokenCheck;
import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.data.Message;
import io.hanbings.server.nikukyu.data.Token;
import io.hanbings.server.nikukyu.exception.NotFoundException;
import io.hanbings.server.nikukyu.model.OAuth;
import io.hanbings.server.nikukyu.model.OAuthClient;
import io.hanbings.server.nikukyu.model.OAuthLog;
import io.hanbings.server.nikukyu.service.OAuthService;
import io.hanbings.server.nikukyu.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0")
@SuppressWarnings("SpellCheckingInspection")
public class OAuthController {
    final TokenService tokenService;
    final OAuthService oAuthService;

    // oauth
    @GetMapping("/oauth")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_READ})
    public Message<?> getOAuth(
            @RequestHeader("Authorization") String bearer
    ) {
        Token token = tokenService.parse(bearer);
        List<OAuth> oauths = oAuthService.getOAuthWithAuid(token.belong());

        return Message.success(oauths);
    }

    @PostMapping("/oauth")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_WRITE})
    public Message<?> createOAuth(
            @RequestHeader("Authorization") String bearer,
            @RequestBody OAuth oAuth
    ) {
        Token token = tokenService.parse(bearer);

        OAuth createdOAuth = oAuthService.createOAuth(
                token.belong(),
                oAuth.redirect() == null ? List.of() : oAuth.redirect(),
                oAuth.access() == null ? List.of() : oAuth.access(),
                oAuth.avatar() == null ? "" : oAuth.avatar(),
                oAuth.name() == null ? "" : oAuth.name(),
                oAuth.description() == null ? "" : oAuth.description(),
                oAuth.homepage() == null ? "" : oAuth.homepage(),
                oAuth.background() == null ? "" : oAuth.background(),
                oAuth.theme() == null ? "" : oAuth.theme(),
                oAuth.policy() == null ? "" : oAuth.policy(),
                oAuth.tos() == null ? "" : oAuth.tos()
        );

        return Message.success(createdOAuth);
    }

    @GetMapping("/oauth/{ouid}")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_READ}, checkOAuth = true)
    public Message<?> getOAuth(
            @RequestHeader("Authorization") String bearer,
            @PathVariable String ouid
    ) {
        @SuppressWarnings("unused") Token token = tokenService.parse(bearer);
        OAuth oAuth = oAuthService.getOAuthWithOuid(ouid);

        if (oAuth == null) {
            throw new NotFoundException(
                    Message.ReturnCode.OAUTH_NOT_FOUND,
                    Message.Messages.OAUTH_NOT_FOUND
            );
        }

        return Message.success(oAuth);
    }

    @PostMapping("/oauth/{ouid}")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_WRITE}, checkOAuth = true)
    public Message<?> updateOAuth(
            @PathVariable String ouid,
            @RequestBody OAuth oAuth
    ) {
        OAuth oldOAuth = oAuthService.getOAuthWithOuid(ouid);

        if (oldOAuth == null) {
            throw new NotFoundException(
                    Message.ReturnCode.OAUTH_NOT_FOUND,
                    Message.Messages.OAUTH_NOT_FOUND
            );
        }

        OAuth updatedOAuth = oAuthService.updateOAuthWithOuid(
                ouid,
                oAuth.redirect() == null ? oldOAuth.redirect() : oAuth.redirect(),
                oAuth.access() == null ? oldOAuth.access() : oAuth.access(),
                oAuth.avatar() == null ? oldOAuth.avatar() : oAuth.avatar(),
                oAuth.name() == null ? oldOAuth.name() : oAuth.name(),
                oAuth.description() == null ? oldOAuth.description() : oAuth.description(),
                oAuth.homepage() == null ? oldOAuth.homepage() : oAuth.homepage(),
                oAuth.background() == null ? oldOAuth.background() : oAuth.background(),
                oAuth.theme() == null ? oldOAuth.theme() : oAuth.theme(),
                oAuth.policy() == null ? oldOAuth.policy() : oAuth.policy(),
                oAuth.tos() == null ? oldOAuth.tos() : oAuth.tos()
        );

        return Message.success(updatedOAuth);
    }

    @DeleteMapping("/oauth/{ouid}")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_WRITE}, checkOAuth = true)
    public Message<?> deleteOAuth(@PathVariable String ouid) {
        oAuthService.deleteOAuthWithOuid(ouid);

        return Message.success(null);
    }

    // client
    @GetMapping("/oauth/{ouid}/client")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_CLIENT_READ}, checkOAuth = true)
    public Message<?> getClient(@PathVariable String ouid) {
        List<OAuthClient> clients = oAuthService.getOAuthClientsWithOuid(ouid);

        return Message.success(clients);
    }

    @PostMapping("/oauth/{ouid}/client")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_CLIENT_WRITE}, checkOAuth = true)
    public Message<?> createClient(@PathVariable String ouid, @RequestParam(value = "expire") String expire) {
        OAuthClient client = oAuthService.createOAuthClient(ouid, Long.parseLong(expire));

        return Message.success(client);
    }

    @GetMapping("/oauth/{ouid}/client/{ocid}")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_CLIENT_READ}, checkOAuth = true)
    public Message<?> getClient(@PathVariable String ouid, @PathVariable String ocid) {
        OAuthClient client = oAuthService.getOAuthClientWithOcid(ocid);

        if (client != null && client.ouid().equals(ouid)) {
            return Message.success(client);
        }

        throw new NotFoundException();
    }

    @PostMapping("/oauth/{ouid}/client/{ocid}")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_CLIENT_WRITE}, checkOAuth = true)
    public Message<?> updateClient(
            @PathVariable String ouid,
            @PathVariable String ocid,
            @RequestParam(value = "expire") String exprie
    ) {
        OAuthClient client = oAuthService.getOAuthClientWithOcid(ocid);

        if (client != null && client.ouid().equals(ouid)) {
            OAuthClient data = oAuthService.updateOAuthClientWithOcid(ocid, Long.parseLong(exprie));
            return Message.success(data);
        }

        throw new NotFoundException();
    }

    @DeleteMapping("/oauth/{ouid}/client/{ocid}")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_CLIENT_WRITE}, checkOAuth = true)
    public Message<?> deleteClient(@PathVariable String ouid, @PathVariable String ocid) {
        OAuthClient client = oAuthService.getOAuthClientWithOcid(ocid);

        if (client != null && client.ouid().equals(ouid)) {
            oAuthService.deleteOAuthClientWithOcid(ocid);
            return Message.success(null);
        }

        throw new NotFoundException();
    }

    // log
    @GetMapping("/oauth/{ouid}/log")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_LOG_READ}, checkOAuth = true)
    public Message<?> getLog(@PathVariable String ouid) {
        return Message.success(oAuthService.getOAuthLogsWithOuid(ouid));
    }

    @GetMapping("/oauth/{ouid}/log/{olid}")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_LOG_READ}, checkOAuth = true)
    public Message<?> getLog(@PathVariable String ouid, @PathVariable String olid) {
        OAuthLog log = oAuthService.getOAuthLogWithOlid(olid);

        if (log != null && log.ouid().equals(ouid)) {
            return Message.success(log);
        }

        throw new NotFoundException();
    }
}
