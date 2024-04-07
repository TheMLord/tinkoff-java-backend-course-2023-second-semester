package edu.java.bot.kafkaErrorHandlers;

import edu.java.models.proto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;

@Slf4j
@RequiredArgsConstructor
public class KafkaListenerUpdateErrorHandler implements KafkaListenerErrorHandler {
    private final KafkaTemplate<String, LinkUpdate.linkUpdateProtoMessage> kafkaTemplate;
    private final String topicName;

    @Override
    public Object handleError(Message<?> message, ListenerExecutionFailedException exception) {
        log.error("error processing kafka link update protobuf message {}", exception.getMessage());

        kafkaTemplate.send(topicName, (LinkUpdate.linkUpdateProtoMessage) message.getPayload());
        return null;
    }
}
