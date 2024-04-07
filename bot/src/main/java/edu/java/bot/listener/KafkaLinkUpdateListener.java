package edu.java.bot.listener;

import edu.java.bot.models.dto.TelegramMessage;
import edu.java.bot.sender.BotMessageSender;
import edu.java.models.proto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaLinkUpdateListener {
    private final BotMessageSender botMessageSender;

    @KafkaListener(topics = "${app.kafka.update-topic-name}", groupId = "linkUpdateProtoMessage-group",
                   containerFactory = "concurrentKafkaListenerContainerFactory")
    public void listen(LinkUpdate.linkUpdateProtoMessage update) {
        log.info("received a message from kafka {}", update);
        update.getTgChatIdsList()
            .forEach(id -> botMessageSender.sendMessage(new TelegramMessage("%s. Ссылка %s".formatted(
                update.getDescription(),
                update.getUrl()
            ), id)).block());
    }

}
