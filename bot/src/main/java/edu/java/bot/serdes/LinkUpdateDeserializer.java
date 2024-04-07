package edu.java.bot.serdes;

import com.google.protobuf.InvalidProtocolBufferException;
import edu.java.models.proto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public class LinkUpdateDeserializer implements Deserializer<LinkUpdate.linkUpdateProtoMessage> {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final String topicName;

    @Override
    public LinkUpdate.linkUpdateProtoMessage deserialize(String topic, byte[] data) {
        try {
            return LinkUpdate.linkUpdateProtoMessage.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            log.error("deserializer error, add message in link_update_deserializer_dlq");
            kafkaTemplate.send(topicName, data).join();
            throw new SerializationException("Error when deserializing byte[] to protobuf", e);
        }
    }
}
