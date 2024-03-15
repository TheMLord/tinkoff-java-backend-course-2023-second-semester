package edu.java.processors;

import edu.java.models.pojo.LinkChanges;
import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public abstract class UriProcessor {
    protected final UriProcessor nextProcessor;

    protected abstract boolean isProcessingUri(URI uri);

    protected abstract Object prepareLinkContent(Object apiArgs);

    protected abstract Object parseUriArgs(String[] uriPaths);

    protected abstract Optional<LinkChanges> prepareUpdate(URI nameLink, String prevContent);

    protected final String[] splitUriPath(URI uri) {
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

    public final Optional<LinkChanges> compareContent(URI nameLink, String prevContent) {
        log.info("checking for {} updates", nameLink.toString());
        if (isProcessingUri(nameLink)) {
            return prepareUpdate(nameLink, prevContent);
        }
        if (this.nextProcessor != null) {
            return nextProcessor.compareContent(nameLink, prevContent);
        }
        return Optional.empty();
    }
}
