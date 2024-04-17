package edu.java.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.models.dto.GithubBranchesDTO;
import edu.java.models.pojo.GithubContent;
import edu.java.models.pojo.GithubUriArg;
import edu.java.models.pojo.LinkChanges;
import edu.java.proxies.GithubProxy;
import java.net.URI;
import java.util.Arrays;
import lombok.SneakyThrows;
import reactor.core.publisher.Mono;

public class GithubProcessor extends UriProcessor {
    private final GithubProxy githubProxy;
    private final ObjectMapper objectMapper;

    private static final String REMOVE_PHRASE = "Deleted ";
    private static final String ADDED_PHRASE = "Added ";
    private static final String BRANCHES_PHRASE = " branch(es):\n";
    private static final String LINE_SEPARATOR = "\n";

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
    protected Mono<LinkChanges> prepareUpdate(URI nameLink, String prevContent) {
        var prevDto = objectMapper.readValue(prevContent, GithubContent.class);
        var newDto = (GithubContent) processUri(nameLink);

        if (!isChangedBranches(prevDto.getGithubBranchesDTO(), newDto.getGithubBranchesDTO())) {
            return Mono.just(
                new LinkChanges(
                    nameLink,
                    prepareChangesDescription(prevDto, newDto),
                    objectMapper.writeValueAsString(newDto)
                )
            );
        }
        return Mono.empty();
    }

    private boolean isChangedBranches(GithubBranchesDTO[] prevBranches, GithubBranchesDTO[] newBranches) {
        var listPrevBranches =
            Arrays.stream(prevBranches).map(GithubBranchesDTO::name).sorted().toList();
        var listNewBranches = Arrays.stream(newBranches).map(GithubBranchesDTO::name).sorted().toList();

        return listPrevBranches.equals(listNewBranches);
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
            changesDescription.append(REMOVE_PHRASE).append(deletedBranches.size()).append(BRANCHES_PHRASE);
            changesDescription.append(String.join(LINE_SEPARATOR, deletedBranches));
            changesDescription.append(LINE_SEPARATOR);
        }

        if (!newBranches.isEmpty()) {
            changesDescription.append(ADDED_PHRASE).append(newBranches.size()).append(BRANCHES_PHRASE);
            changesDescription.append(String.join(LINE_SEPARATOR, newBranches));
        }

        return changesDescription.toString();
    }
}
