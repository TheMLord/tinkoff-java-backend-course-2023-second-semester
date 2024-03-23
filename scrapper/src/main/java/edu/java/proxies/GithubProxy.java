package edu.java.proxies;

import edu.java.models.dto.GithubBranchesDTO;
import edu.java.models.dto.GithubRepositoryDTO;
import java.util.Objects;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class GithubProxy {
    private static final String GITHUB_BASE_URI = "https://api.github.com";
    private final WebClient githubClient;

    public GithubProxy(WebClient.Builder webClientBuilder, String baseUri) {
        this.githubClient = webClientBuilder
            .baseUrl(Objects.requireNonNullElse(baseUri, GITHUB_BASE_URI))
            .build();
    }

    public Mono<GithubRepositoryDTO> getRepositoryRequest(String ownerName, String repositoryName) {
        return this.githubClient
            .get()
            .uri("/repos/{owner}/{repo}", ownerName, repositoryName)
            .retrieve()
            .bodyToMono(GithubRepositoryDTO.class);
    }

    public Mono<GithubBranchesDTO[]> getBranchesRequest(String ownerName, String repositoryName) {
        return this.githubClient
            .get()
            .uri("/repos/{ownerName}/{repositoryName}/branches", ownerName, repositoryName)
            .retrieve()
            .bodyToMono(GithubBranchesDTO[].class);
    }

}
