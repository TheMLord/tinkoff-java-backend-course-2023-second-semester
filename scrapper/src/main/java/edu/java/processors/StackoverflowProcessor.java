package edu.java.processors;

import edu.java.models.pojo.StackoverflowUriArg;
import edu.java.proxies.StackoverflowProxy;
import java.net.URI;

public class StackoverflowProcessor extends UriProcessor {
    private final StackoverflowProxy stackoverflowProxy;

    public StackoverflowProcessor(UriProcessor nextProcessor, StackoverflowProxy stackoverflowProxy) {
        super(nextProcessor);
        this.stackoverflowProxy = stackoverflowProxy;
    }

    @Override
    protected boolean isProcessingUri(URI uri) {
        return uri.getHost().equals("stackoverflow.com");
    }

    @Override
    protected Object prepareLinkContent(Object dto) {
        var stackoverflowApiArgs = (StackoverflowUriArg) dto;
        return stackoverflowProxy.getQuestionRequest(stackoverflowApiArgs.questionId()).block();
    }

    @Override
    protected Object parseUriArgs(String[] uriPaths) {
        return new StackoverflowUriArg(uriPaths[2]);
    }
}
