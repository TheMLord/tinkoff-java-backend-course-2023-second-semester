package edu.java.bot.proxy;

import edu.java.bot.models.dto.api.request.AddLinkRequest;
import edu.java.bot.models.dto.api.request.RemoveLinkRequest;
import edu.java.bot.models.dto.api.response.LinkResponse;
import edu.java.bot.models.dto.api.response.ListLinksResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public class ScrapperProxy {
    private final WebClient scrapperClient;
    private static final String TG_HEADER = "Tg-Chat-Id";
    private static final String TG_REQUEST_PATH = "/tg-chat/{id}";
    private static final String LINK_REQUEST_PATH = "/links";

    public ScrapperProxy(WebClient.Builder webClientBuilder, String baseUri) {
        this.scrapperClient = webClientBuilder
            .baseUrl(baseUri)
            .build();
    }

    public Mono<Void> registerChat(long chatId) {
        return scrapperClient
            .post()
            .uri(TG_REQUEST_PATH, chatId)
            .retrieve()
            .bodyToMono(Void.class);
    }

    public Mono<Void> deleteChat(long chatId) {
        return scrapperClient
            .delete()
            .uri(TG_REQUEST_PATH, chatId)
            .retrieve()
            .bodyToMono(Void.class);
    }

    public Mono<ListLinksResponse> getListLinks(long tgChatId) {
        return scrapperClient
            .get()
            .uri(LINK_REQUEST_PATH)
            .header(TG_HEADER, String.valueOf(tgChatId))
            .retrieve()
            .bodyToMono(ListLinksResponse.class);
    }

    public Mono<LinkResponse> addLink(AddLinkRequest addLinkRequest, long tgChatId) {
        log.info(addLinkRequest.getLink().toString());
        return scrapperClient
            .post()
            .uri(LINK_REQUEST_PATH)
            .header(TG_HEADER, String.valueOf(tgChatId))
            .body(Mono.just(addLinkRequest), AddLinkRequest.class)
            .retrieve()
            .bodyToMono(LinkResponse.class);
    }

    public Mono<LinkResponse> deleteLink(RemoveLinkRequest removeLinkRequest, long tgChatId) {
        return scrapperClient
            .method(HttpMethod.DELETE)
            .uri(LINK_REQUEST_PATH)
            .header(TG_HEADER, String.valueOf(tgChatId))
            .body(Mono.just(removeLinkRequest), RemoveLinkRequest.class)
            .retrieve()
            .bodyToMono(LinkResponse.class);
    }
}
