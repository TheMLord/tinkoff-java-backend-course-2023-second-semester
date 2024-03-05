package edu.java.proxies;

import edu.java.models.dto.api.LinkUpdate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class BotProxy {
    private final WebClient botClient;

    public BotProxy(WebClient.Builder webClientBuilder, String baseUri) {
        this.botClient = webClientBuilder
            .baseUrl(baseUri)
            .build();
    }

    public Mono<Void> pushLinkUpdate(LinkUpdate linkUpdate) {
        return botClient
            .post()
            .uri("/updates")
            .body(linkUpdate, LinkUpdate.class)
            .retrieve()
            .bodyToMono(Void.class);
    }

}
