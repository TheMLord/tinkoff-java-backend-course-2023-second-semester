package edu.java.repository.jooq;

import edu.java.domain.jooq.tables.pojos.Tgchat;
import edu.java.repository.TgChatRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

@RequiredArgsConstructor
public class JooqTgChatRepository implements TgChatRepository {
    private final DSLContext dslContext;

    @Override
    public void add(Long chatId) {

    }

    @Override
    public Optional<Tgchat> findById(Long chatId) {
        return Optional.empty();
    }

    @Override
    public void remove(Long chatId) {

    }
}
