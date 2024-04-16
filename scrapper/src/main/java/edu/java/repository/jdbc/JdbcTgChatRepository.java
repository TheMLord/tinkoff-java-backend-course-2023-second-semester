package edu.java.repository.jdbc;

import edu.java.exceptions.DoubleRegistrationException;
import edu.java.exceptions.NotExistTgChatException;
import edu.java.models.entities.TgChat;
import edu.java.repository.TgChatRepository;
import edu.java.repository.jdbc.utilities.JdbcRowMapperUtil;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * jdbc implementation chat repository.
 */
@Repository
@RequiredArgsConstructor
public class JdbcTgChatRepository implements TgChatRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mono<Void> add(Long chatId) {
        return Mono.fromRunnable(() -> {
            try {
                jdbcTemplate.update(
                    "INSERT INTO tgchats (id, created_at) VALUES (?, ?)",
                    chatId,
                    OffsetDateTime.now()
                );
            } catch (Exception e) {
                throw new DoubleRegistrationException();
            }
        });
    }

    @Override
    public Mono<TgChat> findById(Long chatId) {
        return Mono.defer(() -> {
            var resultChats = jdbcTemplate.query(
                "SELECT * FROM tgchats WHERE id = (?)",
                JdbcRowMapperUtil::mapRowToTgChat,
                chatId
            );
            return resultChats.isEmpty()
                ? Mono.error(new NotExistTgChatException())
                : Mono.just(resultChats.getFirst());
        });
    }

    @Override
    public Mono<Void> remove(Long chatId) {
        return findById(chatId).flatMap(tgChat -> Mono.fromRunnable(() -> jdbcTemplate.update(
            "DELETE FROM tgchats WHERE id = (?)",
            chatId
        )));
    }
}
