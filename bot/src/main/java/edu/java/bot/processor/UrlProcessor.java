package edu.java.bot.processor;

import lombok.AllArgsConstructor;
import java.net.URI;

/**
 * Abstract class url processing.
 */
@AllArgsConstructor
public abstract class UrlProcessor {
    protected UrlProcessor nextProcessor;

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
