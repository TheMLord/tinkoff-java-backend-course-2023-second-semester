package edu.java.bot.serdes;

import edu.java.models.proto.LinkUpdate;
import org.apache.kafka.common.serialization.Serializer;

public class LinkUpdateSerializer implements Serializer<LinkUpdate.linkUpdateProtoMessage> {

    @Override
    public byte[] serialize(String s, LinkUpdate.linkUpdateProtoMessage linkUpdateProtoMessage) {
        return linkUpdateProtoMessage.toByteArray();
    }
}
