package edu.java.services;

import edu.java.models.dto.api.LinkUpdate;
import edu.java.models.entities.Link;
import reactor.core.publisher.Mono;

public interface LinkUpdateService {
    Mono<LinkUpdate> prepareLinkUpdate(Link link);
}
