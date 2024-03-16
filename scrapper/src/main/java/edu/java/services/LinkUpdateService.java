package edu.java.services;

import edu.java.models.dto.api.LinkUpdate;
import edu.java.models.entities.Link;
import java.util.Optional;

public interface LinkUpdateService {
    Optional<LinkUpdate> prepareLinkUpdate(Link link);
}
