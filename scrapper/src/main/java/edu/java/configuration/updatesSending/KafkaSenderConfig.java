package edu.java.configuration.updatesSending;

import edu.java.configuration.ApplicationConfig;
import edu.java.models.proto.LinkUpdate;
import edu.java.senders.BotKafkaSender;
import edu.java.senders.LinkUpdateSender;
import edu.java.serdes.LinkUpdateSerializer;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@EnableKafka
@ConditionalOnProperty(prefix = "app", name = "useQueue", havingValue = "true")
public class KafkaSenderConfig {
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

    @Bean
    public KafkaTemplate<String, LinkUpdate.linkUpdateProtoMessage> protobufKafkaTemplate(
        ApplicationConfig applicationConfig
    ) {
        var config = applicationConfig.kafka();
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrapServers(),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LinkUpdateSerializer.class
        )));
    }

    @Bean
    public LinkUpdateSender kafkaUpdateSender(
        ApplicationConfig applicationConfig,
        KafkaTemplate<String, LinkUpdate.linkUpdateProtoMessage> protobufKafkaTemplate
    ) {
        return new BotKafkaSender(protobufKafkaTemplate, applicationConfig.kafka().updateTopicName());
    }
}
