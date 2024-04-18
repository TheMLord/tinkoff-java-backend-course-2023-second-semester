package edu.java.services;

import edu.java.models.dto.api.LinkUpdate;
import reactor.core.publisher.Flux;

public interface LinkUpdateService {
    Flux<LinkUpdate> prepareLinkUpdate();
}
