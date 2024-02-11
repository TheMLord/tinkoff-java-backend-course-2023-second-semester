package edu.java.bot.processor;

public final class StackOverflowUrlProcessor extends UrlProcessor {
    public StackOverflowUrlProcessor(UrlProcessor processor) {
        super(processor);
    }

    @Override
    protected String getValidHostName() {
        return "stackoverflow.com";
    }
}
