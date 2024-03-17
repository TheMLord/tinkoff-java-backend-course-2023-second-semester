package edu.java.services;

import edu.java.domain.jooq.tables.pojos.Link;
import edu.java.models.dto.api.LinkUpdate;
import java.util.Optional;

public interface LinkUpdateService {
    Optional<LinkUpdate> prepareLinkUpdate(Link link);
}
