package edu.java.configuration.dataAccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.processors.UriProcessor;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.repository.jpa.JpaSubscriptionRepository;
import edu.java.repository.jpa.JpaTgChatRepository;
import edu.java.services.ChatService;
import edu.java.services.LinkService;
import edu.java.services.LinkUpdateCheckService;
import edu.java.services.jpa.JpaLinkService;
import edu.java.services.jpa.JpaLinkUpdateService;
import edu.java.services.jpa.JpaTgChatService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaDataAccessConfig {
    @Bean LinkService jpaLinkService(
        JpaLinkRepository jpaLinkRepository,
        JpaTgChatRepository jpaTgChatRepository,
        JpaSubscriptionRepository jpaSubscriptionRepository,
        UriProcessor uriProcessor,
        ObjectMapper objectMapper
    ) {
        return new JpaLinkService(
            jpaLinkRepository,
            jpaTgChatRepository,
            jpaSubscriptionRepository,
            uriProcessor,
            objectMapper
        );
    }

    @Bean ChatService jpaChatService(JpaTgChatRepository jpaTgChatRepository) {
        return new JpaTgChatService(jpaTgChatRepository);
    }

    @Bean
    LinkUpdateCheckService linkUpdateService(
        JpaLinkRepository jpaLinkRepository,
        UriProcessor uriProcessor,
        JpaSubscriptionRepository jpaSubscriptionRepository
    ) {
        return new JpaLinkUpdateService(jpaLinkRepository, uriProcessor, jpaSubscriptionRepository);
    }
}
