package edu.java.services;

import edu.java.models.dto.api.LinkUpdate;
import edu.java.models.entities.Link;
import java.util.Optional;
import reactor.core.publisher.Mono;

public interface LinkUpdateService {
    Mono<Optional<LinkUpdate>> prepareLinkUpdate(Link link);
}
