package edu.java.proxies;

import edu.java.models.dto.api.LinkUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Objects;

@Slf4j
public class BotProxy {
    private static final String BASE_URI = "http://localhost:8090";
    private final WebClient botClient;

    public BotProxy(WebClient.Builder webClientBuilder, String baseUri) {
        this.botClient = webClientBuilder
            .baseUrl(Objects.requireNonNullElse(baseUri, BASE_URI))
            .build();
    }

    public Mono<Void> pushLinkUpdate(LinkUpdate linkUpdate) {
        log.info("sending an update {}", linkUpdate);

        return botClient
            .post()
            .uri("/updates")
            .body(Mono.just(linkUpdate), LinkUpdate.class)
            .retrieve()
            .bodyToMono(Void.class);
    }

}
