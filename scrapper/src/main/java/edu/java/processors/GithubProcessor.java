package edu.java.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.models.dto.GithubBranchesDTO;
import edu.java.models.pojo.GithubContent;
import edu.java.models.pojo.GithubUriArg;
import edu.java.models.pojo.LinkChanges;
import edu.java.proxies.GithubProxy;
import java.net.URI;
import java.util.Arrays;
import java.util.Optional;
import lombok.SneakyThrows;

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

    @SneakyThrows @Override
    protected Object prepareLinkContent(Object apiArgs) {
        var githubApiArg = (GithubUriArg) apiArgs;
        var owner = githubApiArg.repositoryOwner();
        var repositoryName = githubApiArg.repositoryName();

        var repositoryInfo =
            githubProxy.getRepositoryRequest(owner, repositoryName).block();
        var branchesInfo =
            githubProxy.getBranchesRequest(owner, repositoryName).block();

        return GithubContent
            .builder()
            .githubRepositoryDTO(repositoryInfo)
            .githubBranchesDTO(branchesInfo)
            .build();
    }

    @Override
    protected Object parseUriArgs(String[] uriPaths) {
        return new GithubUriArg(uriPaths[1], uriPaths[2]);
    }

    @SneakyThrows
    @Override
    protected Optional<LinkChanges> prepareUpdate(URI nameLink, String prevContent) {
        var prevDto = objectMapper.readValue(prevContent, GithubContent.class);
        var newDto = (GithubContent) processUri(nameLink);

        if (!isChangedBranches(prevDto.getGithubBranchesDTO(), newDto.getGithubBranchesDTO())) {
            return Optional.of(
                new LinkChanges(
                    nameLink,
                    prepareChangesDescription(prevDto, newDto),
                    objectMapper.writeValueAsString(newDto)
                )
            );
        }
        return Optional.empty();
    }

    private boolean isChangedBranches(GithubBranchesDTO[] prevBranches, GithubBranchesDTO[] newBranches) {
        Arrays.sort(prevBranches);
        Arrays.sort(newBranches);
        return Arrays.equals(prevBranches, newBranches);
    }

    private String prepareChangesDescription(GithubContent prevContent, GithubContent newContent) {
        var prevList = Arrays.stream(prevContent.getGithubBranchesDTO()).map(GithubBranchesDTO::name).toList();
        var newList = Arrays.stream(newContent.getGithubBranchesDTO()).map(GithubBranchesDTO::name).toList();

        var deletedBranches = prevList.stream()
            .filter(branch -> !newList.contains(branch))
            .toList();
        var newBranches = newList.stream()
            .filter(branch -> !prevList.contains(branch) && !deletedBranches.contains(branch))
            .toList();

        var changesDescription = new StringBuilder();

        if (!deletedBranches.isEmpty()) {
            changesDescription.append("Удалено ").append(deletedBranches.size()).append(" веток:\n");
            changesDescription.append(String.join("\n", deletedBranches));
            changesDescription.append("\n");
        }

        if (!newBranches.isEmpty()) {
            changesDescription.append("Добавлено ").append(newBranches.size()).append(" веток:\n");
            changesDescription.append(String.join("\n", newBranches));
        }

        return changesDescription.toString();
    }
}
