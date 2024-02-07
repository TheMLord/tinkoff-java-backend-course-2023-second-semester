package edu.java.bot.configuration;

import edu.java.bot.processor.url_processor.GitHubUrlProcessor;
import edu.java.bot.processor.url_processor.StackOverflowUrlProcessor;
import edu.java.bot.processor.url_processor.UrlProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class url processors.
 */
@Configuration
public class UrlProcessorConfig {
    /**
     * Method creating a link handler bean for tracking by the chain of responsibility pattern.
     */
    @Bean
    UrlProcessor urlProcessor() {
        return new StackOverflowUrlProcessor(
            new GitHubUrlProcessor(null)
        );
    }
}
