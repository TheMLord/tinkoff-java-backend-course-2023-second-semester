package edu.java.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.processors.GithubProcessor;
import edu.java.processors.StackoverflowProcessor;
import edu.java.processors.UriProcessor;
import edu.java.proxies.GithubProxy;
import edu.java.proxies.StackoverflowProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UriProcessorConfig {
    @Bean
    public UriProcessor uriProcessor(
        GithubProxy githubProxy,
        StackoverflowProxy stackoverflowProxy,
        ObjectMapper objectMapper
    ) {
        return new GithubProcessor(
            new StackoverflowProcessor(null, stackoverflowProxy, objectMapper),
            githubProxy,
            objectMapper
        );
    }
}
