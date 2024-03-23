package edu.java.schedulers;

import edu.java.proxies.BotProxy;
import edu.java.repository.LinkRepository;
import edu.java.services.LinkUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.OffsetDateTime;

/**
 * Scheduler class for periodically checking for updates to the content of links stored
 * in the links table and monitored by chats.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public final class LinkUpdaterScheduler {
    private final LinkUpdateService linkUpdateService;
    private final LinkRepository linkRepository;
    private final BotProxy botProxy;

    /**
     * Scheduled method that starts a periodic search for updates
     * for a link and sends them via a nat Proxy if available.
     */
    @Scheduled(fixedDelayString = "#{@scheduler.interval().toMillis()}")
    private void update() {
        log.info("executing the update method");

        linkRepository.findAllByTime(OffsetDateTime.now().minusHours(1))
            .subscribe(links -> links.forEach(link -> linkUpdateService.prepareLinkUpdate(link)
                .subscribe(optionalUpdate -> optionalUpdate.map(botProxy::pushLinkUpdate))));
    }
}
