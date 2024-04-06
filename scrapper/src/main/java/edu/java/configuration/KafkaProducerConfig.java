package edu.java.configuration;

import edu.java.models.proto.LinkUpdate;
import edu.java.serdes.LinkUpdateSerializer;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public KafkaTemplate<String, LinkUpdate.linkUpdateProtoMessage> protobufKafkaTemplate(
        ApplicationConfig applicationConfig
    ) {
        var config = applicationConfig.kafka();
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, prepareServersConfig(config.bootstrapServers()),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LinkUpdateSerializer.class
        )));
    }

    private static String prepareServersConfig(String[] servers) {
        var n = servers.length;
        var serversString = new StringBuilder(n + n - 1);
        for (int i = 0; i < n - 1; i++) {
            serversString.append(servers[i]);
            serversString.append(",");
        }
        serversString.append(servers[n - 1]);
        return serversString.toString();
    }
}
