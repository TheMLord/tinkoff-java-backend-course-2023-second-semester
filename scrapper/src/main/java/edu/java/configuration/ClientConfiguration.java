package edu.java.configuration;

import edu.java.proxies.GithubProxy;
import edu.java.proxies.StackoverflowProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    @Bean
    public GithubProxy githubProxy(ApplicationConfig applicationConfig) {
        return new GithubProxy(applicationConfig.clientBaseUrl().githubUri());
    }

    @Bean
    public StackoverflowProxy stackoverflowProxy(ApplicationConfig applicationConfig) {
        return new StackoverflowProxy(applicationConfig.clientBaseUrl().stackoverflowUri());
    }
}
