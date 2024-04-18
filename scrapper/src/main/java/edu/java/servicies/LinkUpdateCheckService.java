package edu.java.servicies;

import edu.java.models.dto.api.LinkUpdate;
import reactor.core.publisher.Flux;

public interface LinkUpdateCheckService {
    Flux<LinkUpdate> prepareLinkUpdate();
}
