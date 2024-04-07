package edu.java.senders;

import edu.java.models.dto.api.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BotKafkaSender implements LinkUpdateSender {
    private final KafkaTemplate<String, edu.java.models.proto.LinkUpdate.linkUpdateProtoMessage> kafkaTemplate;
    private final String topicName;

    @Override
    public Mono<Void> pushLinkUpdate(LinkUpdate linkUpdate) {
        return Mono.fromFuture(kafkaTemplate.send(topicName, buildMessage(linkUpdate))).then()
            .onErrorMap(throwable -> throwable);
    }

    private static edu.java.models.proto.LinkUpdate.linkUpdateProtoMessage buildMessage(LinkUpdate linkUpdate) {
        var builder = edu.java.models.proto.LinkUpdate.linkUpdateProtoMessage.newBuilder()
            .setId(linkUpdate.getId())
            .setDescription(linkUpdate.getDescription())
            .setUrl(linkUpdate.getUrl().toString())
            .addAllTgChatIds(linkUpdate.getTgChatIds());

        return builder.build();
    }
}
