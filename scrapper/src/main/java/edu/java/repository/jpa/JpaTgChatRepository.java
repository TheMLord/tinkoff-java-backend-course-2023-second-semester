package edu.java.repository.jpa;

import edu.java.domain.jpa.TgChats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTgChatRepository extends JpaRepository<TgChats, Long> {

}
