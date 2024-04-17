package edu.java.configuration.dataAccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.processors.UriProcessor;
import edu.java.repository.LinkDao;
import edu.java.repository.LinkRepository;
import edu.java.repository.TgChatRepository;
import edu.java.repository.jooq.JooqLinkDao;
import edu.java.repository.jooq.JooqLinkRepository;
import edu.java.repository.jooq.JooqTgChatRepository;
import edu.java.services.ChatService;
import edu.java.services.LinkService;
import edu.java.services.LinkUpdateService;
import edu.java.services.jooq.JooqChatService;
import edu.java.services.jooq.JooqLinkService;
import edu.java.services.jooq.JooqLinkUpdateService;
import org.jooq.DSLContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
public class JooqDataAccessConfig {
    @Bean LinkRepository jooqLinkRepository(DSLContext dslContext) {
        return new JooqLinkRepository(dslContext);
    }

    @Bean TgChatRepository jooqTgChatRepository(DSLContext dslContext) {
        return new JooqTgChatRepository(dslContext);
    }

    @Bean LinkDao jooqLinkDao(
        DSLContext dslContext,
        LinkRepository linkRepository,
        TgChatRepository tgChatRepository,
        UriProcessor uriProcessor,
        ObjectMapper objectMapper
    ) {
        return new JooqLinkDao(linkRepository, tgChatRepository, dslContext, objectMapper, uriProcessor);
    }

    @Bean LinkService jooqLinkService(LinkDao linkDao) {
        return new JooqLinkService(linkDao);
    }

    @Bean ChatService jooqChatService(TgChatRepository tgChatRepository) {
        return new JooqChatService(tgChatRepository);
    }

    @Bean LinkUpdateService linkUpdateService(
        UriProcessor uriProcessor,
        LinkDao linkDao,
        LinkRepository linkRepository
    ) {
        return new JooqLinkUpdateService(uriProcessor, linkDao, linkRepository);
    }

}
