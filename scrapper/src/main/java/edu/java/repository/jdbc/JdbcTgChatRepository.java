package edu.java.repository.jdbc;

import edu.java.exceptions.DoubleRegistrationException;
import edu.java.exceptions.NotExistTgChatException;
import edu.java.models.entities.TgChat;
import edu.java.repository.TgChatRepository;
import edu.java.repository.jdbc.utilities.JdbcRowMapperUtil;
import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * jdbc implementation chat repository.
 */
@Repository
@RequiredArgsConstructor
public class JdbcTgChatRepository implements TgChatRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(Long chatId) {
        try {
            jdbcTemplate.update(
                "INSERT INTO tgchats (id, created_at) VALUES (?, ?)",
                chatId,
                OffsetDateTime.now()
            );
        } catch (Exception e) {
            throw new DoubleRegistrationException();
        }
    }

    @Override
    public Optional<TgChat> findById(Long chatId) {
        var resultChats = jdbcTemplate.query(
            "SELECT * FROM tgchats WHERE id = (?)",
            JdbcRowMapperUtil::mapRowToTgChat,
            chatId
        );
        return resultChats.isEmpty() ? Optional.empty() : Optional.of(resultChats.getFirst());
    }

    @Override
    public void remove(Long chatId) {
        if (this.findById(chatId).isEmpty()) {
            throw new NotExistTgChatException();
        }
        jdbcTemplate.update("DELETE FROM tgchats WHERE id = (?)", chatId);
    }
}
