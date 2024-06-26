package edu.java.repository.jooq;

import edu.java.domain.jooq.pojos.Tgchats;
import edu.java.exceptions.DoubleRegistrationException;
import edu.java.exceptions.NotExistTgChatException;
import edu.java.repository.TgChatRepository;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import reactor.core.publisher.Mono;
import static edu.java.domain.jooq.tables.Tgchats.TGCHATS;

@RequiredArgsConstructor
public class JooqTgChatRepository implements TgChatRepository {
    private final DSLContext dslContext;

    @Override
    public Mono<Void> add(Long chatId) {
        return Mono.fromRunnable(() -> {
            try {
                dslContext.insertInto(TGCHATS)
                    .columns(TGCHATS.ID, TGCHATS.CREATED_AT)
                    .values(chatId, OffsetDateTime.now())
                    .execute();
            } catch (Exception e) {
                throw new DoubleRegistrationException();
            }
        });
    }

    @Override
    public Mono<Tgchats> findById(Long chatId) {
        return Mono.defer(() -> {
            var resultChats = dslContext
                .select(TGCHATS.fields())
                .from(TGCHATS)
                .where(TGCHATS.ID.eq(chatId))
                .fetchInto(Tgchats.class);
            return resultChats.isEmpty()
                ? Mono.error(new NotExistTgChatException())
                : Mono.just(resultChats.getFirst());
        });
    }

    @Override
    public Mono<Void> remove(Long chatId) {
        return findById(chatId)
            .flatMap(tgchat -> Mono.fromRunnable(() ->
                dslContext.delete(TGCHATS).where(TGCHATS.ID.eq(chatId)).execute()));

    }
}
