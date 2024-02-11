package edu.java.bot.processor;

import java.net.URI;

/**
 * Abstract class url processing.
 */
public abstract class UrlProcessor {
    protected UrlProcessor nextProcessor;

    /**
     * Class constructor.
     */
    public UrlProcessor(UrlProcessor processor) {
        this.nextProcessor = processor;
    }

    /**
     * Method that returns a valid handler host.
     */
    protected abstract String getValidHostName();

    /**
     * Method that checks whether it is possible to process URIs
     */
    public final boolean isValidUrl(URI url) {
        if (url.getHost().equals(getValidHostName())) {
            return true;
        }
        if (this.nextProcessor != null) {
            return this.nextProcessor.isValidUrl(url);
        }
        return false;
    }
}
