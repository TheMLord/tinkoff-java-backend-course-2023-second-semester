package edu.java.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ConditionalOnProperty(prefix = "app", value = "scheduler.enable", havingValue = "true")
@EnableScheduling
public class SchedulingConfiguration {
}
