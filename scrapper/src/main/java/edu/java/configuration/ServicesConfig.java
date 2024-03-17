package edu.java.configuration;

import edu.java.processors.UriProcessor;
import edu.java.repository.LinkDao;
import edu.java.repository.LinkRepository;
import edu.java.repository.TgChatRepository;
import edu.java.services.ChatService;
import edu.java.services.LinkService;
import edu.java.services.LinkUpdateService;
import edu.java.services.jdbc.JdbcChatService;
import edu.java.services.jdbc.JdbcLinkService;
import edu.java.services.jdbc.JdbcLinkUpdateService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServicesConfig {
    @Bean
    public ChatService chatService(
        ApplicationConfig applicationConfig,
        TgChatRepository tgChatRepository
    ) {
        return switch (applicationConfig.dataAccessTechnology()) {
            case "JDBC" -> new JdbcChatService(tgChatRepository);
            default -> throw new IllegalStateException("Unexpected value: " + applicationConfig.dataAccessTechnology());
        };
    }

    @Bean
    public LinkService linkService(
        ApplicationConfig applicationConfig,
        LinkDao linkDao
    ) {
        return switch (applicationConfig.dataAccessTechnology()) {
            case "JDBC" -> new JdbcLinkService(linkDao);
            default -> throw new IllegalStateException("Unexpected value: " + applicationConfig.dataAccessTechnology());
        };
    }

    @Bean
    public LinkUpdateService linkUpdateService(
        ApplicationConfig applicationConfig,
        UriProcessor uriProcessor,
        LinkRepository linkRepository,
        LinkDao linkDao
    ) {
        return switch (applicationConfig.dataAccessTechnology()) {
            case "JDBC" -> new JdbcLinkUpdateService(uriProcessor, linkDao, linkRepository);
            default -> throw new IllegalStateException("Unexpected value: " + applicationConfig.dataAccessTechnology());
        };
    }

}
