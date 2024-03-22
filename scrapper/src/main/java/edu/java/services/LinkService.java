package edu.java.services;

import edu.java.models.dto.api.response.LinkResponse;
import edu.java.models.dto.api.response.ListLinksResponse;
import reactor.core.publisher.Mono;
import java.net.URI;

/**
 * Interface of the link service.
 * <p>
 * Describes a contract for managing requests related to links that arrive at controllers.
 */
public interface LinkService {
    /**
     * Method of adding a link in tracking by a specific chat.
     *
     * @param chatId  id chat that is going to track the link.
     * @param linkUri URL of the link that the chat wants to track
     * @return If successful, it returns a LinkResponse with the
     *     id of the link that the database will assign and the name that was received when adding.
     */
    Mono<LinkResponse> addLink(long chatId, URI linkUri);

    /**
     * Method that removes a link from tracking for a specific user.
     *
     * @param chatId  id chat that is going to untrack the link.
     * @param linkUri URL of the link that the chat wants to delete in the track.
     * @return If successful, it returns a response to the link with the
     *     link ID that the link had in the database and the name that was received when it was deleted.
     */
    Mono<LinkResponse> removeLink(long chatId, URI linkUri);

    /**
     * Method that returns a prepared response with a list of all
     * links tracked by a specific chat in LinkResponse format.
     *
     * @param chatId id chat
     * @return a ListLinksResponse, which contains a
     *     list of LinkResponse, each of which contains the id and uri of the
     *     link that a particular chat is tracking
     */
    Mono<ListLinksResponse> getListLinks(long chatId);
}
