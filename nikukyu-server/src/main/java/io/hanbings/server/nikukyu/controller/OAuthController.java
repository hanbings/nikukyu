package io.hanbings.server.nikukyu.controller;

import io.hanbings.server.nikukyu.annotation.NikukyuTokenCheck;
import io.hanbings.server.nikukyu.content.AccessType;
import io.hanbings.server.nikukyu.data.Message;
import io.hanbings.server.nikukyu.data.Token;
import io.hanbings.server.nikukyu.exception.NotFoundException;
import io.hanbings.server.nikukyu.model.OAuth;
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
            @RequestParam("name") String name,
            @RequestParam(value = "access") List<AccessType> access,
            @RequestParam(value = "redirect", required = false, defaultValue = "") String redirect,
            @RequestParam(value = "avatar", required = false, defaultValue = "") String avatar,
            @RequestParam(value = "description", required = false, defaultValue = "") String description,
            @RequestParam(value = "homepage", required = false, defaultValue = "") String homepage,
            @RequestParam(value = "background", required = false, defaultValue = "") String background,
            @RequestParam(value = "theme", required = false, defaultValue = "") String theme,
            @RequestParam(value = "policy", required = false, defaultValue = "") String policy,
            @RequestParam(value = "tos", required = false, defaultValue = "") String tos
    ) {
        Token token = tokenService.parse(bearer);
        List<String> redirects = List.of(redirect.split(","));

        OAuth oAuth = oAuthService.createOAuth(
                token.belong(),
                redirects,
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

        return Message.success(oAuth);
    }

    @GetMapping("/oauth/{ouid}")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_READ}, checkOAuth = true)
    public Message<?> getOAuth(
            @RequestHeader("Authorization") String bearer,
            @PathVariable String ouid
    ) {
        Token token = tokenService.parse(bearer);
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
    public Message<?> updateOAuth(@PathVariable String ouid) {
        return null;
    }

    @DeleteMapping("/oauth/{ouid}")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_WRITE}, checkOAuth = true)
    public Message<?> deleteOAuth(@PathVariable String ouid) {
        return null;
    }

    // client
    @GetMapping("/oauth/{ouid}/client")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_CLIENT_READ}, checkOAuth = true)
    public Message<?> getClient(@PathVariable String ouid) {
        return null;
    }

    @PostMapping("/oauth/{ouid}/client")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_CLIENT_WRITE}, checkOAuth = true)
    public Message<?> createClient(@PathVariable String ouid) {
        return null;
    }

    @GetMapping("/oauth/{ouid}/client/{ocid}")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_CLIENT_READ}, checkOAuth = true)
    public Message<?> getClient(@PathVariable String ouid, @PathVariable String ocid) {
        return null;
    }

    @PostMapping("/oauth/{ouid}/client/{ocid}")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_CLIENT_WRITE}, checkOAuth = true)
    public Message<?> updateClient(@PathVariable String ouid, @PathVariable String ocid) {
        return null;
    }

    @DeleteMapping("/oauth/{ouid}/client/{ocid}")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_CLIENT_WRITE}, checkOAuth = true)
    public Message<?> deleteClient(@PathVariable String ouid, @PathVariable String ocid) {
        return null;
    }

    // log
    @GetMapping("/oauth/{ouid}/log")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_LOG_READ}, checkOAuth = true)
    public Message<?> getLog(@PathVariable String ouid) {
        return null;
    }

    @GetMapping("/oauth/{ouid}/log/{olid}")
    @NikukyuTokenCheck(access = {AccessType.OAUTH_LOG_READ}, checkOAuth = true)
    public Message<?> getLog(@PathVariable String ouid, @PathVariable String olid) {
        return null;
    }
}
