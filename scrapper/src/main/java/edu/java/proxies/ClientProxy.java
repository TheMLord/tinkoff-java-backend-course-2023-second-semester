package edu.java.proxies;

import edu.java.dto.GithubDTO;
import edu.java.dto.StackoverflowDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public final class ClientProxy {
    private final WebClient githubClient;
    private final WebClient stackoverflowClient;

    public Mono<GithubDTO> createGithubRequest(String ownerName, String repositoryName) {
        return githubClient
            .get()
            .uri(ownerName + "/" + repositoryName)
            .retrieve()
            .bodyToMono(GithubDTO.class);

    }

    public Mono<StackoverflowDTO> createStackoverflowRequest(String questionId) {
        return stackoverflowClient
            .get()
            .uri(questionId + "/?site=stackoverflow&filter=withbody")
            .retrieve()
            .bodyToMono(StackoverflowDTO.class);
    }
}
