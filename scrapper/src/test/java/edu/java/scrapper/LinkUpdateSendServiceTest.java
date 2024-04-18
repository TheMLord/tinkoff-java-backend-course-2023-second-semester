package edu.java.scrapper;

import edu.java.ScrapperApplication;
import edu.java.models.dto.api.LinkUpdate;
import edu.java.senders.LinkUpdateSender;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest(classes = ScrapperApplication.class)
@TestPropertySource(locations = "classpath:test")
@DirtiesContext
class LinkUpdateSendServiceTest {
    @Autowired LinkUpdateSender botKafkaSender;

    KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(Map.of(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29091, localhost:29092, localhost:29093",
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class
    )));

//    @Test
    @DisplayName("Test of sending incorrect data to the topic")
    void testOfSendingIncorrectDataToTheTopic() {
        kafkaTemplate.send("scrapper.link_update", "hello").join();

    }

    //    @Test
    @DisplayName("Test of sending correct data to the topic")
    void testOfSendingCorrectDataToTheTopic() {
        var update = new LinkUpdate(
            1L,
            URI.create("https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester"),
            """
                Added 1 branch(es):
                createdAccount""",
            List.of(667559701L)
        );
        botKafkaSender.pushLinkUpdate(update).block();
    }
}
