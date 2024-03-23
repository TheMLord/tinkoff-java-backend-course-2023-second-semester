package edu.java.domain.jpa;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.EqualsAndHashCode;

@Embeddable
@EqualsAndHashCode
public class SubscriptionsPK implements Serializable {
    private Long chat;
    private Long link;
}
