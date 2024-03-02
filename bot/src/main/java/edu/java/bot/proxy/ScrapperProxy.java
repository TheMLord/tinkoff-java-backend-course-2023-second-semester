package edu.java.bot.proxy;

import edu.java.bot.models.dto.api.request.AddLinkRequest;
import edu.java.bot.models.dto.api.request.RemoveLinkRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ScrapperProxy {
    private final WebClient scrapperClient;

    public ScrapperProxy(WebClient.Builder webClientBuilder, String baseUri) {
        this.scrapperClient = webClientBuilder
            .baseUrl(baseUri)
            .build();
    }

    public Mono<?> registerChat(long chatId) {
        return scrapperClient
            .post()
            .uri("/tg-chat/{id}", chatId)
            .retrieve()
            .bodyToMono(Object.class);
    }

    public Mono<?> deleteChat(long chatId) {
        return scrapperClient
            .delete()
            .uri("/tg-chat/{id}", chatId)
            .retrieve()
            .bodyToMono(Object.class);
    }

    public Mono<?> getListLinks(long tgChatId) {
        return scrapperClient
            .get()
            .uri("/links")
            .header("Tg-Chat-Id", String.valueOf(tgChatId))
            .retrieve()
            .bodyToMono(Object.class);
    }

    public Mono<?> addLink(AddLinkRequest addLinkRequest, long tgChatId) {
        return scrapperClient
            .post()
            .uri("/links")
            .header("Tg-Chat-Id", String.valueOf(tgChatId))
            .body(Mono.just(addLinkRequest), AddLinkRequest.class)
            .retrieve()
            .bodyToMono(Object.class);
    }

    public Mono<?> deleteLink(RemoveLinkRequest removeLinkRequest, long tgChatId) {
        return scrapperClient
            .method(HttpMethod.DELETE)
            .uri("/links")
            .header("Tg-Chat-Id", String.valueOf(tgChatId))
            .body(Mono.just(removeLinkRequest), RemoveLinkRequest.class)
            .retrieve()
            .bodyToMono(Object.class);
    }
}
