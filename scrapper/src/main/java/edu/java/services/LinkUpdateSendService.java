package edu.java.services;

import edu.java.models.dto.api.LinkUpdate;
import edu.java.senders.LinkUpdateSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class LinkUpdateSendService implements SendUpdateService {
    private final LinkUpdateSender linkUpdateSender;

    @Override
    public void sendUpdate(LinkUpdate linkUpdate) {
        linkUpdateSender.pushLinkUpdate(linkUpdate)
            .onErrorResume(throwable -> {
                log.error("error sending update {}", throwable.getMessage());
                return Mono.empty();
            })
            .subscribe();
    }
}
