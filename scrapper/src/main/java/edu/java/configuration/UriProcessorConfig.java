package edu.java.configuration;

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
    public UriProcessor uriProcessor(GithubProxy githubProxy, StackoverflowProxy stackoverflowProxy) {
        return new GithubProcessor(
            new StackoverflowProcessor(null, stackoverflowProxy),
            githubProxy
        );
    }
}
