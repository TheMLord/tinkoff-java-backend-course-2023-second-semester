package edu.java.bot.configuration;

import edu.java.bot.serdes.LinkUpdateDeserializer;
import edu.java.models.proto.LinkUpdate;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

@Configuration
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

    @Bean
    public KafkaTemplate<String, Byte[]> dlqKafkaTemplate(
        ApplicationConfig applicationConfig
    ) {
        var config = applicationConfig.kafka();
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrapServers(),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class
        )));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LinkUpdate.linkUpdateProtoMessage>
    concurrentKafkaListenerContainerFactory(ApplicationConfig applicationConfig) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, LinkUpdate.linkUpdateProtoMessage>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationConfig.kafka().bootstrapServers(),
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class,
            ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, LinkUpdateDeserializer.class.getName()
        )));

        return factory;
    }
}
