package io.hanbings.nikukyu.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@SuppressWarnings("SpellCheckingInspection")
public class IndexController {
    Map<String, String> version = Map.of(
            "version", "1.0.0",
            "repository", "https://github.com/hanbings/nikukyu",
            "project", "Nikukyu",
            "author", "hanbings <hanbings@hanbings.io>",
            "license", "Apache-2.0"
    );

    @GetMapping(value = {"/version"})
    public Object index() {
        return version;
    }

    @GetMapping(value = {"/", "/api", "/api/v0"})
    public Object api() {
        return Map.of("nikukyu", "Hypermedia Controls unsupported yet");
    }
}
