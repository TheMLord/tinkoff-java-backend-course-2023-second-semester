package edu.java.services;

import edu.java.domain.pojos.Links;
import edu.java.models.dto.api.LinkUpdate;
import reactor.core.publisher.Mono;

public interface LinkUpdateService {
    Mono<LinkUpdate> prepareLinkUpdate(Links link);
}
