package edu.java.services;

import edu.java.exceptions.AlreadyTrackLinkException;
import edu.java.exceptions.NotExistLinkException;
import edu.java.exceptions.NotFoundUserException;
import edu.java.models.dto.api.response.LinkResponse;
import edu.java.models.dto.api.response.ListLinksResponse;
import edu.java.repository.UserRepository;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LinkService {
    private final UserRepository userRepository;

    public ListLinksResponse prepareUserLinks(long tgChatId) {
        try {
            var linksUser = userRepository.findUserSites(tgChatId);

            return new ListLinksResponse(
                linksUser.stream().map(link -> new LinkResponse(1L, link)).toList(),
                linksUser.size()
            );
        } catch (Exception e) {
            throw new NotFoundUserException(e);
        }
    }

    public LinkResponse addLink(long tgChatId, URI linkToTrack) {
        var listLinks = userRepository.findUserSites(tgChatId);
        if (listLinks.contains(linkToTrack)) {
            throw new AlreadyTrackLinkException();
        }
        userRepository.appendLink(tgChatId, linkToTrack);
        return new LinkResponse(1L, linkToTrack);
    }

    public LinkResponse deleteLink(long tgChatId, URI linkToUntrack) {
        var listLinks = userRepository.findUserSites(tgChatId);
        if (!listLinks.contains(linkToUntrack)) {
            throw new NotExistLinkException();
        }
        userRepository.deleteUserLink(tgChatId, linkToUntrack);
        return new LinkResponse(1L, linkToUntrack);
    }

}
