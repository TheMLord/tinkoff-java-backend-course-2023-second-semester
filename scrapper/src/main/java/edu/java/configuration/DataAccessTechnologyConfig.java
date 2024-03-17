package edu.java.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.processors.UriProcessor;
import edu.java.repository.LinkDao;
import edu.java.repository.LinkRepository;
import edu.java.repository.TgChatRepository;
import edu.java.repository.jdbc.JdbcLinkDao;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.repository.jdbc.JdbcTgChatRepository;
import edu.java.repository.jooq.JooqLinkDao;
import edu.java.repository.jooq.JooqLinkRepository;
import edu.java.repository.jooq.JooqTgChatRepository;
import jakarta.activation.DataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DataAccessTechnologyConfig {

    @Bean
    public TgChatRepository tgChatRepository(
        ApplicationConfig applicationConfig,
        JdbcTemplate jdbcTemplate,
        DSLContext dslContext
    ) {
        return switch (applicationConfig.dataAccessTechnology()) {
            case "JDBC" -> new JdbcTgChatRepository(jdbcTemplate);
            case "JOOQ" -> new JooqTgChatRepository(dslContext);
            default -> throw new IllegalStateException("Unexpected value: " + applicationConfig.dataAccessTechnology());
        };
    }

    @Bean
    public LinkRepository linkRepository(
        ApplicationConfig applicationConfig,
        JdbcTemplate jdbcTemplate,
        DSLContext dslContext
    ) {
        return switch (applicationConfig.dataAccessTechnology()) {
            case "JDBC" -> new JdbcLinkRepository(jdbcTemplate);
            case "JOOQ" -> new JooqLinkRepository(dslContext);
            default -> throw new IllegalStateException("Unexpected value: " + applicationConfig.dataAccessTechnology());
        };
    }

    @Bean
    public LinkDao linkDao(
        ApplicationConfig applicationConfig,
        JdbcTemplate jdbcTemplate,
        DSLContext dslContext,
        LinkRepository linkRepository,
        TgChatRepository tgChatRepository,
        UriProcessor uriProcessor,
        ObjectMapper objectMapper
    ) {
        return switch (applicationConfig.dataAccessTechnology()) {
            case "JDBC" -> new JdbcLinkDao(jdbcTemplate, tgChatRepository, linkRepository, uriProcessor, objectMapper);
            case "JOOQ" -> new JooqLinkDao(linkRepository, tgChatRepository, dslContext, objectMapper, uriProcessor);
            default -> throw new IllegalStateException("Unexpected value: " + applicationConfig.dataAccessTechnology());
        };
    }
}
