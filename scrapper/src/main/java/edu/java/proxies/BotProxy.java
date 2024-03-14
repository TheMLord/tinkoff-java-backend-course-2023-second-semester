package edu.java.proxies;

import edu.java.models.dto.api.LinkUpdate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Objects;

public class BotProxy {
    private static final String BASE_URI = "http://localhost:8090";
    private final WebClient botClient;

    public BotProxy(WebClient.Builder webClientBuilder, String baseUri) {
        this.botClient = webClientBuilder
            .baseUrl(Objects.requireNonNullElse(baseUri, BASE_URI))
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
