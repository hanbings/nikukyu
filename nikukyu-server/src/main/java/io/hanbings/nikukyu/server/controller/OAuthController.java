package io.hanbings.nikukyu.server.controller;

import io.hanbings.nikukyu.server.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0")
public class OAuthController {
    final OAuthService oauthService;
}
