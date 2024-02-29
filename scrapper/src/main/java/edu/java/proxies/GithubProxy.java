package edu.java.proxies;

import edu.java.proxies.dto.GithubDTO;
import java.util.Objects;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class GithubProxy {
    private static final String GITHUB_BASE_URI = "https://api.github.com";
    private final WebClient githubClient;

    public GithubProxy(String baseUri) {
        this.githubClient = WebClient
            .builder()
            .baseUrl(Objects.requireNonNullElse(baseUri, GITHUB_BASE_URI))
            .build();
    }

    public Mono<GithubDTO> getRepositoryRequest(String ownerName, String repositoryName) {
        return this.githubClient
            .get()
            .uri("/repos/{owner}/{repo}", ownerName, repositoryName)
            .retrieve()
            .bodyToMono(GithubDTO.class);
    }

}