package edu.java.bot.proxy;

import edu.java.bot.controller.dto.ApiErrorResponse;
import edu.java.bot.controller.dto.request.AddLinkRequest;
import edu.java.bot.controller.dto.request.RemoveLinkRequest;
import edu.java.bot.controller.dto.response.LinkResponse;
import edu.java.bot.controller.dto.response.ListLinksResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ScrapperProxy {
    private final WebClient scrapperClient;

    public Mono<Void> registerChat(long chatId) {
        return scrapperClient
            .post()
            .uri("/tg-chat/{id}", chatId)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(ApiErrorResponse.class))
            .bodyToMono(Void.class);
    }

    public Mono<Void> deleteChat(long chatId) {
        return scrapperClient
            .delete()
            .uri("/tg-chat/{id}", chatId)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(ApiErrorResponse.class))
            .bodyToMono(Void.class);
    }

    public Mono<ListLinksResponse> getListLinks() {
        return scrapperClient
            .get()
            .uri("/links")
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(ApiErrorResponse.class))
            .bodyToMono(ListLinksResponse.class);
    }

    public Mono<LinkResponse> addLink(AddLinkRequest addLinkRequest) {
        return scrapperClient
            .post()
            .uri("/links")
            .body(Mono.just(addLinkRequest), AddLinkRequest.class)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(ApiErrorResponse.class))
            .bodyToMono(LinkResponse.class);
    }

    public Mono<LinkResponse> deleteLink(RemoveLinkRequest removeLinkRequest) {
        return scrapperClient
            .method(HttpMethod.DELETE)
            .uri("/links")
            .body(Mono.just(removeLinkRequest), RemoveLinkRequest.class)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(ApiErrorResponse.class))
            .bodyToMono(LinkResponse.class);
    }
}
