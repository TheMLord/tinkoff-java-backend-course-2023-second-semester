package edu.java.configuration.dataAccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.processors.UriProcessor;
import edu.java.repository.LinkDao;
import edu.java.repository.LinkRepository;
import edu.java.repository.TgChatRepository;
import edu.java.repository.jdbc.JdbcLinkDao;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.repository.jdbc.JdbcTgChatRepository;
import edu.java.services.ChatService;
import edu.java.services.LinkService;
import edu.java.services.LinkUpdateService;
import edu.java.services.jdbc.JdbcChatService;
import edu.java.services.jdbc.JdbcLinkService;
import edu.java.services.jdbc.JdbcLinkUpdateService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcDataAccessConfig {

    @Bean LinkRepository jdbcLinkRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcLinkRepository(jdbcTemplate);
    }

    @Bean TgChatRepository jdbcTgChatRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcTgChatRepository(jdbcTemplate);
    }

    @Bean LinkDao jdbcLinkDao(
        JdbcTemplate jdbcTemplate,
        LinkRepository linkRepository,
        TgChatRepository tgChatRepository,
        UriProcessor uriProcessor,
        ObjectMapper objectMapper
    ) {
        return new JdbcLinkDao(jdbcTemplate, tgChatRepository, linkRepository, uriProcessor, objectMapper);
    }

    @Bean LinkService jdbcLinkService(LinkDao linkDao) {
        return new JdbcLinkService(linkDao);
    }

    @Bean ChatService jdbcChatService(TgChatRepository tgChatRepository) {
        return new JdbcChatService(tgChatRepository);
    }

    @Bean LinkUpdateService linkUpdateService(
        UriProcessor uriProcessor,
        LinkDao linkDao,
        LinkRepository linkRepository
    ) {
        return new JdbcLinkUpdateService(uriProcessor, linkDao, linkRepository);
    }
}
