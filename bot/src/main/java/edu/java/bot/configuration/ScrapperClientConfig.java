package edu.java.bot.configuration;

import edu.java.bot.proxy.ScrapperProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ScrapperClientConfig {
    @Bean
    public ScrapperProxy scrapperProxy(WebClient.Builder webClientBuilder, ApplicationConfig appConfig) {
        return new ScrapperProxy(
            webClientBuilder,
            appConfig.scrapperBaseUri()
        );
    }
}
