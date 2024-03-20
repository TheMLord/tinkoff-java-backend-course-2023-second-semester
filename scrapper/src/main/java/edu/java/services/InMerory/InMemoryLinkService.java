package edu.java.services.InMerory;

import edu.java.exceptions.AlreadyTrackLinkException;
import edu.java.exceptions.NotExistLinkException;
import edu.java.exceptions.NotExistTgChatException;
import edu.java.models.dto.api.response.LinkResponse;
import edu.java.models.dto.api.response.ListLinksResponse;
import edu.java.repository.InMemoryChatRepository;
import edu.java.services.LinkService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InMemoryLinkService implements LinkService {
    private final InMemoryChatRepository chatRepository;

    @Override
    public LinkResponse addLink(long tgChatId, URI linkToTrack) {
        var listLinks = chatRepository.findUserSites(tgChatId);
        if (listLinks.contains(linkToTrack)) {
            throw new AlreadyTrackLinkException();
        }
        chatRepository.appendLink(tgChatId, linkToTrack);
        return new LinkResponse(1L, linkToTrack);
    }

    @Override
    public LinkResponse removeLink(long chatId, URI linkUri) {
        var listLinks = chatRepository.findUserSites(chatId);
        if (!listLinks.contains(linkUri)) {
            throw new NotExistLinkException();
        }
        chatRepository.deleteUserLink(chatId, linkUri);
        return new LinkResponse(1L, linkUri);
    }

    @Override
    public ListLinksResponse getListLinks(long chatId) {
        try {
            var linksUser = chatRepository.findUserSites(chatId);

            return new ListLinksResponse(
                linksUser.stream().map(link -> new LinkResponse(1L, link)).toList(),
                linksUser.size()
            );
        } catch (Exception e) {
            throw new NotExistTgChatException();
        }
    }
}
