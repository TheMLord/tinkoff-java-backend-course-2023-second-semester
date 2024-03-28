package edu.java.bot.configuration;

import edu.java.bot.proxy.ScrapperProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

/**
 * Configuration of the client that makes requests to the Scrapper application.
 */
@Configuration
public class ScrapperClientConfig {
    /**
     * Method of creating and configuring the scrapperProxy bean
     *
     * @param webClientBuilder interface for building a WebClient.
     * @param appConfig        application configuration properties by app prefix/
     */
    @Bean
    public ScrapperProxy scrapperProxy(
        WebClient.Builder webClientBuilder,
        ApplicationConfig appConfig,
        Retry retryPolicy
    ) {
        return new ScrapperProxy(
            webClientBuilder,
            appConfig.scrapperBaseUri(),
            retryPolicy
        );
    }
}
