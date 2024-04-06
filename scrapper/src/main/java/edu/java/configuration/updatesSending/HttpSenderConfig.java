package edu.java.configuration.updatesSending;

import edu.java.configuration.ApplicationConfig;
import edu.java.senders.BotHttpSender;
import edu.java.senders.LinkUpdateSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "useQueue", havingValue = "false")
public class HttpSenderConfig {
    @Bean
    public LinkUpdateSender httpUpdateSender(
        WebClient.Builder webClientBuilder,
        ApplicationConfig applicationConfig
    ) {
        return new BotHttpSender(webClientBuilder, applicationConfig.clientBaseUrl().botUrl());
    }
}
