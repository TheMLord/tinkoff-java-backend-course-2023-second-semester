package edu.java.configuration;

import java.util.Objects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfiguration {
    private static final String GITHUB_URI = "https://api.github.com/repos/";
    private static final String STACKOVERFLOW_URI = "https://api.stackexchange.com/2.3/questions/";

    @Bean
    public WebClient githubClient(ApplicationConfig appConfig) {
        var configUrl = appConfig.clientBaseUrl().githubUrl();
        return WebClient
            .builder()
            .baseUrl(Objects.requireNonNullElse(configUrl, GITHUB_URI))
            .build();
    }

    @Bean
    public WebClient stackoverflowClient(ApplicationConfig appConfig) {
        var configUrl = appConfig.clientBaseUrl().stackoverflowUrl();
        return WebClient
            .builder()
            .baseUrl(Objects.requireNonNullElse(configUrl, STACKOVERFLOW_URI))
            .build();
    }

    @Bean
    public WebClient botClient(ApplicationConfig appConfig) {
        return WebClient
            .builder()
            .baseUrl(appConfig.clientBaseUrl().botUrl())
            .build();
    }
}
