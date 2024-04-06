package edu.java.configuration;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
@Slf4j
public class KafkaConfig {
    @Bean
    public NewTopic[] topics(ApplicationConfig applicationConfig) {
        var topicsProperty = applicationConfig.kafka().topicsProperty();
        var topics = new NewTopic[topicsProperty.length];

        for (int i = 0; i < topicsProperty.length; i++) {
            topics[i] = (TopicBuilder.name(topicsProperty[i].topicName())
                .partitions(topicsProperty[i].numberPartitions())
                .replicas(topicsProperty[i].replicationFactor())
                .build()
            );
        }
        return topics;
    }

    @Bean
    public KafkaAdmin kafkaAdmin(ApplicationConfig applicationConfig, NewTopic[] topics) {
        var kafkaAdmin = new KafkaAdmin(Map.of(
            AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, applicationConfig.kafka().bootstrapServers()
        ));
        kafkaAdmin.createOrModifyTopics(topics);
        return kafkaAdmin;
    }
}
