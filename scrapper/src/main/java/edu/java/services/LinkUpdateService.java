package edu.java.services;

import edu.java.domain.pojos.Links;
import edu.java.models.dto.api.LinkUpdate;
import java.util.Optional;
import reactor.core.publisher.Mono;

public interface LinkUpdateService {
    Mono<Optional<LinkUpdate>> prepareLinkUpdate(Links link);
}
