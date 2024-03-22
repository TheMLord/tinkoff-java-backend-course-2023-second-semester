package edu.java.services;

import edu.java.models.dto.api.LinkUpdate;
import edu.java.models.entities.Link;
import reactor.core.publisher.Mono;
import java.util.Optional;

public interface LinkUpdateService {
    Mono<Optional<LinkUpdate>> prepareLinkUpdate(Link link);
}
