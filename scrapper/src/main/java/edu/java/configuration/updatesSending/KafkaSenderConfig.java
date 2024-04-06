package edu.java.configuration.updatesSending;

import edu.java.models.proto.LinkUpdate;
import edu.java.senders.BotKafkaSender;
import edu.java.senders.LinkUpdateSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "useQueue", havingValue = "true")
public class KafkaSenderConfig {
    @Bean
    public LinkUpdateSender kafkaUpdateSender(KafkaTemplate<String, LinkUpdate.linkUpdateProtoMessage> kafkaTemplate) {
        return new BotKafkaSender(kafkaTemplate);
    }
}
