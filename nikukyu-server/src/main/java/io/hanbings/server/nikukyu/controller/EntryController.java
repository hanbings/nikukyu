package io.hanbings.server.nikukyu.controller;

import io.hanbings.server.nikukyu.content.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/")
public class EntryController {
    @GetMapping("/")
    public Map<String, String> index() {
        return config();
    }

    @SuppressWarnings("all")
    @GetMapping("/version")
    public Map<String, String> config() {
        return Map.of(
                "name", Resources.PROJECT,
                "version", Resources.VERSION,
                "repository", Resources.REPOSITORY,
                "author", Resources.AUTHOR,
                "license", Resources.LICENSE
        );
    }
}
