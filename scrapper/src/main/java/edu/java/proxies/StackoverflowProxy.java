package edu.java.proxies;

import edu.java.proxies.dto.StackoverflowDTO;
import java.util.Objects;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class StackoverflowProxy {
    private static final String STACKOVERFLOW_BASE_URI = "https://api.stackexchange.com/2.3";

    private final WebClient stackoverflowClient;

    public StackoverflowProxy(String baseUri) {
        this.stackoverflowClient = WebClient
            .builder()
            .baseUrl(Objects.requireNonNullElse(baseUri, STACKOVERFLOW_BASE_URI))
            .build();
    }

    public Mono<StackoverflowDTO> getQuestionRequest(String questionId) {
        return this.stackoverflowClient
            .get()
            .uri("/questions/{questionId}/?site=stackoverflow&filter=withbody", questionId)
            .retrieve()
            .bodyToMono(StackoverflowDTO.class);
    }

}
