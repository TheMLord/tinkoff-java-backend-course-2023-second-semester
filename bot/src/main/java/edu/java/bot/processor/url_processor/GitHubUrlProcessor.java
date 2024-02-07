package edu.java.bot.processor.url_processor;

public final class GitHubUrlProcessor extends UrlProcessor {
    public GitHubUrlProcessor(UrlProcessor processor) {
        super(processor);
    }

    @Override
    protected String getValidHostName() {
        return "github.com";
    }

}
