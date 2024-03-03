package edu.java.configuration;

import edu.java.proxies.BotProxy;
import edu.java.proxies.GithubProxy;
import edu.java.proxies.StackoverflowProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public GithubProxy githubProxy(
        WebClient.Builder webClientBuilder,
        ApplicationConfig applicationConfig
    ) {
        return new GithubProxy(webClientBuilder, applicationConfig.clientBaseUrl().githubUri());
    }

    @Bean
    public StackoverflowProxy stackoverflowProxy(
        WebClient.Builder webClientBuilder,
        ApplicationConfig applicationConfig
    ) {
        return new StackoverflowProxy(webClientBuilder, applicationConfig.clientBaseUrl().stackoverflowUri());
    }

    @Bean
    public BotProxy botClient(
        WebClient.Builder webClientBuilder,
        ApplicationConfig applicationConfig
    ) {
        return new BotProxy(webClientBuilder, applicationConfig.clientBaseUrl().botUrl());
    }
}
