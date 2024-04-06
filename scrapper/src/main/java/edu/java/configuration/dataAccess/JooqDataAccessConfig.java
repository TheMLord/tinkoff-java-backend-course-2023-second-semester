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
import edu.java.services.LinkUpdateCheckService;
import edu.java.services.jooq.JooqChatService;
import edu.java.services.jooq.JooqLinkService;
import edu.java.services.jooq.JooqLinkUpdateService;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jooq.JooqExceptionTranslator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
public class JooqDataAccessConfig {

    @Bean
    public DSLContext dslContext(DataSourceConnectionProvider dataSourceConnectionProvider) {
        return new DefaultDSLContext(new DefaultConfiguration()
            .set(dataSourceConnectionProvider)
            .set(SQLDialect.POSTGRES)
            .set(new Settings()
                .withRenderSchema(false)
                .withRenderFormatted(true)
                .withRenderQuotedNames(RenderQuotedNames.NEVER))
            .set(new DefaultExecuteListenerProvider(new JooqExceptionTranslator())));
    }

    @Bean LinkRepository joocLinkRepository(DSLContext dslContext) {
        return new JooqLinkRepository(dslContext);
    }

    @Bean TgChatRepository joocTgChatRepository(DSLContext dslContext) {
        return new JooqTgChatRepository(dslContext);
    }

    @Bean LinkDao joocLinkDao(
        DSLContext dslContext,
        LinkRepository linkRepository,
        TgChatRepository tgChatRepository,
        UriProcessor uriProcessor,
        ObjectMapper objectMapper
    ) {
        return new JooqLinkDao(linkRepository, tgChatRepository, dslContext, objectMapper, uriProcessor);
    }

    @Bean LinkService joocLinkService(LinkDao linkDao) {
        return new JooqLinkService(linkDao);
    }

    @Bean ChatService joocChatService(TgChatRepository tgChatRepository) {
        return new JooqChatService(tgChatRepository);
    }

    @Bean LinkUpdateCheckService linkUpdateService(
        UriProcessor uriProcessor,
        LinkDao linkDao,
        LinkRepository linkRepository
    ) {
        return new JooqLinkUpdateService(uriProcessor, linkDao, linkRepository);
    }

}
