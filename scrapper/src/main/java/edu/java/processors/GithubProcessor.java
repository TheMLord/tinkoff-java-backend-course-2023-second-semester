package edu.java.processors;

import edu.java.models.pojo.GithubUriArg;
import edu.java.proxies.GithubProxy;
import java.net.URI;

public class GithubProcessor extends UriProcessor {
    private final GithubProxy githubProxy;

    public GithubProcessor(UriProcessor nextProcessor, GithubProxy githubProxy) {
        super(nextProcessor);
        this.githubProxy = githubProxy;
    }

    @Override
    protected boolean isProcessingUri(URI uri) {
        return uri.getHost().equals("github.com");
    }

    @Override
    protected Object prepareLinkContent(Object args) {
        var githubApiArg = (GithubUriArg) args;
        return githubProxy.getRepositoryRequest(githubApiArg.repositoryOwner(), githubApiArg.repositoryName()).block();
    }

    @Override
    protected Object parseUriArgs(String[] uriPaths) {
        return new GithubUriArg(uriPaths[1], uriPaths[2]);
    }
}
