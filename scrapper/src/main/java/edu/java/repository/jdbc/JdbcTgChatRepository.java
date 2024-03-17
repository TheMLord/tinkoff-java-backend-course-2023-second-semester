package edu.java.repository.jdbc;

import edu.java.domain.jooq.tables.pojos.Tgchat;
import edu.java.exceptions.DoubleRegistrationException;
import edu.java.exceptions.NotExistTgChatException;
import edu.java.repository.TgChatRepository;
import edu.java.repository.jdbc.utilities.JdbcRowMapperUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * jdbc implementation chat repository.
 */
@RequiredArgsConstructor
public class JdbcTgChatRepository implements TgChatRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(Long chatId) {
        try {
            jdbcTemplate.update("INSERT INTO tgchat (chat_id) VALUES (?)", chatId);
        } catch (Exception e) {
            throw new DoubleRegistrationException();
        }
    }

    @Override
    public Optional<Tgchat> findById(Long chatId) {
        var resultChats = jdbcTemplate.query(
            "SELECT * FROM tgchat WHERE chat_id = (?)",
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
        jdbcTemplate.update("DELETE FROM tgchat WHERE chat_id = (?)", chatId);
    }
}
