package edu.java.models.entities;

import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class Subscriptions {
    private final Long chatId;
    private final Long linkId;
    private final OffsetDateTime createdAt;
    private final String createdAd;
}
