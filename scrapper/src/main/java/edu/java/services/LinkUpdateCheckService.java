package edu.java.services;

import edu.java.models.dto.api.LinkUpdate;
import java.util.Optional;
import reactor.core.publisher.Flux;

public interface LinkUpdateCheckService {
    Flux<Optional<LinkUpdate>> prepareLinkUpdate();
}
