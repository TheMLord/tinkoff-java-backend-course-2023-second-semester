package edu.java.schedulers;

import edu.java.proxies.BotProxy;
import edu.java.repository.LinkRepository;
import edu.java.services.LinkUpdateService;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public final class LinkUpdaterScheduler {
    private final LinkUpdateService linkUpdateService;
    private final LinkRepository linkRepository;
    private final BotProxy botProxy;

    @Scheduled(fixedDelayString = "#{@scheduler.interval().toMillis()}")
    private void update() {
        log.info("executing the update method");

        linkRepository.findAllByTime(OffsetDateTime.now().minusHours(1))
            .forEach(link -> linkUpdateService.prepareLinkUpdate(link)
                .ifPresent(botProxy::pushLinkUpdate));
    }
}
