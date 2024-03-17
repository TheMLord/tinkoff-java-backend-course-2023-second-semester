package edu.java.repository.jooq;

import edu.java.domain.jooq.tables.pojos.Tgchat;
import edu.java.exceptions.DoubleRegistrationException;
import edu.java.exceptions.NotExistTgChatException;
import edu.java.repository.TgChatRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import static edu.java.domain.jooq.tables.Tgchat.TGCHAT;

@RequiredArgsConstructor
public class JooqTgChatRepository implements TgChatRepository {
    private final DSLContext dslContext;

    @Override
    public void add(Long chatId) {
        try {
            dslContext.insertInto(TGCHAT)
                .columns(TGCHAT.CHAT_ID)
                .values(chatId)
                .execute();
        } catch (Exception e) {
            throw new DoubleRegistrationException();
        }
    }

    @Override
    public Optional<Tgchat> findById(Long chatId) {
        var chats = dslContext
            .select(TGCHAT.fields())
            .from(TGCHAT)
            .where(TGCHAT.CHAT_ID.eq(chatId))
            .fetch()
            .into(Tgchat.class);
        return chats.isEmpty() ? Optional.empty() : Optional.of(chats.getFirst());
    }

    @Override
    public void remove(Long chatId) {
        if (this.findById(chatId).isEmpty()) {
            throw new NotExistTgChatException();
        }
        dslContext.delete(TGCHAT).where(TGCHAT.CHAT_ID.eq(chatId)).execute();
    }
}
