package edu.java.repository.jpa;

import edu.java.domain.jpa.Links;
import edu.java.domain.jpa.Subscriptions;
import edu.java.domain.jpa.SubscriptionsPK;
import edu.java.domain.jpa.TgChats;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaSubscriptionRepository extends JpaRepository<Subscriptions, SubscriptionsPK> {
    Optional<Subscriptions> findByChatAndLink(TgChats tgChats, Links links);

    List<Subscriptions> findAllByChat(TgChats tgChats);

    void deleteByChatAndLink(TgChats tgChats, Links links);

    List<Subscriptions> findAllByLink(Links links);
}
