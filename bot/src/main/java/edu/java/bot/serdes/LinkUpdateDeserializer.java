package edu.java.bot.serdes;

import com.google.protobuf.InvalidProtocolBufferException;
import edu.java.models.proto.LinkUpdate;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.jetbrains.annotations.NotNull;

public class LinkUpdateDeserializer implements Deserializer<LinkUpdate.linkUpdateProtoMessage> {
    @Override
    public LinkUpdate.@NotNull linkUpdateProtoMessage deserialize(String topic, byte[] data) {
        try {
            return LinkUpdate.linkUpdateProtoMessage.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            throw new SerializationException(e);
        }
    }
}
