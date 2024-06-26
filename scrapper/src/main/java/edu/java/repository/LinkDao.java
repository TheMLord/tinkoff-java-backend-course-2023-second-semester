package edu.java.repository;

import edu.java.domain.jooq.pojos.Links;
import edu.java.exceptions.AlreadyTrackLinkException;
import edu.java.exceptions.NotExistLinkException;
import edu.java.exceptions.NotExistTgChatException;
import edu.java.exceptions.NotTrackLinkException;
import java.net.URI;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interface describing the LinkDao contract.
 */
public interface LinkDao {
    /**
     * Method adds a link to the track chat.
     * Method also adds a link to the database if it does not already exist.
     *
     * @param chatId the ID of the chat that wants to track the link.
     * @param uri    the URL of the link that the chat wants to track.
     * @return the Link object, which contains the link name and the identifier assigned to it in the database.
     * @throws NotExistTgChatException   if the chat is not registered.
     * @throws AlreadyTrackLinkException if the chat is already tracking this link.
     */
    Mono<Links> add(Long chatId, URI uri) throws NotExistTgChatException, AlreadyTrackLinkException;

    /**
     * Method adds a link to the untrack chat.
     * The method also removes the link entity from the database if no one is tracking it anymore.
     *
     * @param chatId the ID of the chat that wants to untrack the link.
     * @param uri    the URL of the link that the chat wants to track.
     * @return the Link object, which contains the link name and the identifier assigned to it in the database.
     * @throws NotExistTgChatException if the chat is not registered.
     * @throws NotExistLinkException   if there is no such link in the database.
     * @throws NotTrackLinkException   if the chat does not track such a link
     */
    Mono<Links> remove(Long chatId, URI uri)
        throws NotExistTgChatException, NotExistLinkException, NotTrackLinkException;

    /**
     * Method that collects all the links that the chat is tracking at the time of the request.
     *
     * @param chatId the ID of the chat for which you want to get all the links it tracks.
     * @return a list of Link objects ranging from 0 to n.
     * @throws NotExistTgChatException if the chat is not registered.
     */

    Flux<Links> getAllLinksInRelation(Long chatId) throws NotExistTgChatException;

    /**
     * Method searches for all users who are tracking the link
     *
     * @param uriId the ID of the link for which you need to find the chats tracking it.
     * @return list of chat IDs from the tgChat table that track this link.
     */
    Flux<Long> findAllIdTgChatWhoTrackLink(Long uriId);
}
