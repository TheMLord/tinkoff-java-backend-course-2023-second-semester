package edu.java.repository.jdbc.utilities;

//import edu.java.models.entities.Link;
//import edu.java.models.entities.TgChat;

import edu.java.domain.jooq.pojos.Links;
import edu.java.domain.jooq.pojos.Subscriptions;
import edu.java.domain.jooq.pojos.Tgchats;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

/**
 * Class with utilities method for mapper entities.
 */
@SuppressWarnings("MultipleStringLiterals")
@NoArgsConstructor
public final class JdbcRowMapperUtil {
    private static final String LINK_TABLE_COLUMN_ID = "id";
    private static final String LINK_TABLE_COLUMN_LINK_NAME = "link_uri";
    private static final String LINK_TABLE_COLUMN_CREATE_AT = "created_at";
    private static final String LINK_TABLE_COLUMN_CREATE_BY = "created_by";
    private static final String LINK_TABLE_COLUMN_CONTENT = "content";
    private static final String LINK_TABLE_COLUMN_LAST_MODIFYING = "last_modifying";

    private static final String TG_CHAT_TABLE_COLUMN_ID = "id";
    private static final String TG_CHAT_TABLE_COLUMN_CREATED_AT = "created_at";
    private static final String TG_CHAT_TABLE_COLUMN_CREATED_BY = "created_by";

    private static final String RELATION_TABLE_COLUMN_CHAT_ID = "chat_id";
    private static final String RELATION_TABLE_COLUMN_LINK_ID = "link_id";
    private static final String RELATION_TABLE_COLUMN_CREATED_AT = "created_at";
    private static final String RELATION_TABLE_COLUMN_CREATED_BY = "created_by";

    /**
     * Method of mapping the string received from the query to the Link entity.
     *
     * @param row    row set from the table.
     * @param rowNum number row from the received data from the query.
     * @return Link entity.
     */
    @SneakyThrows
    public static Links mapRowToLink(ResultSet row, int rowNum) {
        return new Links(
            row.getLong(LINK_TABLE_COLUMN_ID),
            row.getString(LINK_TABLE_COLUMN_LINK_NAME),
            row.getObject(LINK_TABLE_COLUMN_CREATE_AT, OffsetDateTime.class),
            row.getString(LINK_TABLE_COLUMN_CREATE_BY),
            row.getString(LINK_TABLE_COLUMN_CONTENT),
            row.getObject(LINK_TABLE_COLUMN_LAST_MODIFYING, OffsetDateTime.class)
        );
    }

    /**
     * Method of mapping the string received from the query to the Relation entity.
     *
     * @param row    row set from the table.
     * @param rowNum number row from the received data from the query.
     * @return Relation entity.
     */
    @SneakyThrows
    public static Subscriptions mapRowToRelation(ResultSet row, int rowNum) {
        return new Subscriptions(
            row.getLong(RELATION_TABLE_COLUMN_CHAT_ID),
            row.getLong(RELATION_TABLE_COLUMN_LINK_ID),
            row.getObject(RELATION_TABLE_COLUMN_CREATED_AT, OffsetDateTime.class),
            row.getString(RELATION_TABLE_COLUMN_CREATED_BY)
        );
    }

    /**
     * Mapping method the term of the database query to the chat ID from the tgchat table.
     *
     * @param row    row set from the table.
     * @param rowNum number row from the received data from the query.
     * @return id chat.
     */
    @SneakyThrows
    public static Long mapRowToChatId(ResultSet row, int rowNum) {
        return row.getLong(TG_CHAT_TABLE_COLUMN_ID);
    }

    /**
     * Mapping method the term of the database query to the entity TgChat from the tgchat table.
     *
     * @param row    row set from the table.
     * @param rowNum number row from the received data from the query.
     * @return TgChat entity from table tgchat.
     */
    @SneakyThrows
    public static Tgchats mapRowToTgChat(ResultSet row, int rowNum) {
        return new Tgchats(
            row.getLong(TG_CHAT_TABLE_COLUMN_ID),
            row.getObject(TG_CHAT_TABLE_COLUMN_CREATED_AT, OffsetDateTime.class),
            row.getString(TG_CHAT_TABLE_COLUMN_CREATED_BY)
        );
    }
}
