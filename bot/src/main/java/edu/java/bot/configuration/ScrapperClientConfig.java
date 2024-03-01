package edu.java.bot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ScrapperClientConfig {
    @Bean
    public WebClient scrapperClient(ApplicationConfig appConfig) {
        return WebClient
            .builder()
            .baseUrl(appConfig.scrapperBaseUri())
            .build();
    }
}
