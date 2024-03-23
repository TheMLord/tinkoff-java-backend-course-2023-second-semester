package edu.java.configuration;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.boot.autoconfigure.jooq.JooqExceptionTranslator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JooqDSCContextConfig {

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
}
