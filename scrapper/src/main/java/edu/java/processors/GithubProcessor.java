package edu.java.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.models.dto.GithubDTO;
import edu.java.models.pojo.GithubUriArg;
import edu.java.models.pojo.LinkChanges;
import edu.java.proxies.GithubProxy;
import java.net.URI;
import java.util.Objects;
import lombok.SneakyThrows;
import reactor.core.publisher.Mono;

public class GithubProcessor extends UriProcessor {
    private final GithubProxy githubProxy;
    private final ObjectMapper objectMapper;

    public GithubProcessor(UriProcessor nextProcessor, GithubProxy githubProxy, ObjectMapper objectMapper) {
        super(nextProcessor);
        this.githubProxy = githubProxy;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean isProcessingUri(URI uri) {
        return uri.getHost().equals("github.com");
    }

    @Override
    protected Object prepareLinkContent(Object apiArgs) {
        var githubApiArg = (GithubUriArg) apiArgs;
        return githubProxy.getRepositoryRequest(githubApiArg.repositoryOwner(), githubApiArg.repositoryName()).block();
    }

    @Override
    protected Object parseUriArgs(String[] uriPaths) {
        return new GithubUriArg(uriPaths[1], uriPaths[2]);
    }

    @SneakyThrows
    @Override
    protected Mono<LinkChanges> prepareUpdate(URI nameLink, String prevContent) {
        var prevDto = objectMapper.readValue(prevContent, GithubDTO.class);
        var newDto = (GithubDTO) processUri(nameLink);

        if (!prevDto.pushedAt().equals(Objects.requireNonNull(newDto).pushedAt())) {
            return Mono.just(
                new LinkChanges(nameLink, "Есть изменения",
                    objectMapper.writeValueAsString(newDto)
                )
            );
        }
        return Mono.empty();
    }
}
