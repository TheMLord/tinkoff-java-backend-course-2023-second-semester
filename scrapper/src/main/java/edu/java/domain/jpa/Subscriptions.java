package edu.java.domain.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "subscriptions", uniqueConstraints = {@UniqueConstraint(columnNames = {"chat_id", "link_id"})})
@IdClass(SubscriptionsPK.class)
public class Subscriptions {
    @Id
    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private TgChats chat;
    @Id
    @ManyToOne
    @JoinColumn(name = "link_id", nullable = false)
    private Links link;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "created_by", insertable = false, nullable = false, columnDefinition = "TEXT DEFAULT 'themlord'")
    private String createdBy;
}
