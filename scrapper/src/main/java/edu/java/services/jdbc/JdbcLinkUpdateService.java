package edu.java.services.jdbc;

import edu.java.models.dto.api.LinkUpdate;
import edu.java.models.entities.Link;
import edu.java.processors.UriProcessor;
import edu.java.repository.LinkDao;
import edu.java.repository.LinkRepository;
import edu.java.services.LinkUpdateService;
import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JdbcLinkUpdateService implements LinkUpdateService {
    private final UriProcessor uriProcessor;
    private final LinkDao linkDao;
    private final LinkRepository linkRepository;

    @Override
    public Optional<LinkUpdate> prepareLinkUpdate(Link link) {
        var linkId = link.getId();

        var updateOptional = uriProcessor.compareContent(link.getLinkName(), link.getContent());
        linkRepository.updateLastModifying(linkId, OffsetDateTime.now());

        return updateOptional
            .map(linkChanges -> {
                    linkRepository.updateContent(linkId, linkChanges.newContent());

                    return new LinkUpdate(
                        link.getId(),
                        linkChanges.linkName(),
                        linkChanges.descriptionChanges(),
                        linkDao.findAllIdTgChatWhoTrackLink(link.getId())
                    );
                }
            );

    }
}
