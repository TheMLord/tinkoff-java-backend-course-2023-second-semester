package edu.java.senders;

import edu.java.models.dto.api.LinkUpdate;
import reactor.core.publisher.Mono;

public interface LinkUpdateSender {
    Mono<Void> pushLinkUpdate(LinkUpdate linkUpdate);
}
