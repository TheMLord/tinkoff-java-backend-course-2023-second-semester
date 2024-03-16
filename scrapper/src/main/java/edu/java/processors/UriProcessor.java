package edu.java.processors;

import edu.java.models.pojo.LinkChanges;
import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * link processing contract.
 */
@RequiredArgsConstructor
@Slf4j
public abstract class UriProcessor {
    protected final UriProcessor nextProcessor;

    /**
     * Method that checks whether the UriProcessor implementation can handle this link.
     *
     * @param uri the address of the verification link.
     * @return true if the link can be processed and false in other case.
     */
    protected abstract boolean isProcessingUri(URI uri);

    protected abstract Object prepareLinkContent(Object apiArgs);

    /**
     * Парсер аргументов из ссылки для выполнения обработки
     *
     * @param uriPaths the link paths received from splitUriPath.
     *                 They represent separate parts of the
     *                 path of the original link after separating them by /.
     * @return Wrapper object for arguments for a specific processor.
     */
    protected abstract Object parseUriArgs(String[] uriPaths);

    /**
     * Method of checking for content updates at the link.
     *
     * @param nameLink    link uri to check.
     * @param prevContent previous link content.
     * @return an LinkChanges with information about changes or an empty Optional
     *     if there are no changes in the content of the link.
     */
    protected abstract Optional<LinkChanges> prepareUpdate(URI nameLink, String prevContent);

    /**
     * Method of splitting the link path by /.
     *
     * @param uri uri to split.
     * @return array of link paths.
     */
    protected final String[] splitUriPath(URI uri) {
        return uri.getPath().split("/");
    }

    /**
     * Method of processing the link to get the content
     *
     * @return dto with content
     */
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

    /**
     * Method link content comparison. The method will make a request to receive
     * information about the link via the api, if there are changes,
     * they will be recorded in LinkChanges.
     *
     * @param nameLink    the uri of the link to check for content updates.
     * @param prevContent current content.
     */
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
