package edu.java.processors;

import edu.java.models.pojo.LinkChanges;
import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class UriProcessor {
    protected final UriProcessor nextProcessor;

    protected abstract boolean isProcessingUri(URI uri);

    protected abstract Object prepareLinkContent(Object apiArgs);

    protected abstract Object parseUriArgs(String[] uriPaths);

    protected String[] splitUriPath(URI uri) {
        return uri.getPath().split("/");
    }

    public final Object processUri(URI uri) {
        if (isProcessingUri(uri)) {
            return prepareLinkContent(
                parseUriArgs(
                    splitUriPath(uri)
                )
            );
        }
        if (this.nextProcessor != null) {
            return nextProcessor.processUri(uri);
        }
        return null;
    }

    public abstract Optional<LinkChanges> compareContent(URI nameLink, String prevContent);

}
