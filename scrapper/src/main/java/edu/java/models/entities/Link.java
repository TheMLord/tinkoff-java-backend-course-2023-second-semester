package edu.java.models.entities;

import java.net.URI;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class Link {
    private final Long id;
    private final URI linkUri;
    private final OffsetDateTime createdAt;
    private final String createdBy;
    private final String content;
    private final OffsetDateTime lastModifying;
}
