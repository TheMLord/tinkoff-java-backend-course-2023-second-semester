package edu.java.domain.jooq;

import edu.java.domain.jooq.tables.Links;
import edu.java.domain.jooq.tables.Subscriptions;
import edu.java.domain.jooq.tables.Tgchats;
import edu.java.domain.jooq.tables.records.LinksRecord;
import edu.java.domain.jooq.tables.records.SubscriptionsRecord;
import edu.java.domain.jooq.tables.records.TgchatsRecord;
import javax.annotation.processing.Generated;
import org.jooq.ForeignKey;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in the
 * default schema.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.7"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({"all", "unchecked", "rawtypes", "this-escape", "MemberName", "EmptyLineSeparator"})
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<LinksRecord> CONSTRAINT_4 = Internal.createUniqueKey(Links.LINKS, DSL.name("CONSTRAINT_4"), new TableField[] { Links.LINKS.ID }, true);
    public static final UniqueKey<LinksRecord> CONSTRAINT_45 = Internal.createUniqueKey(Links.LINKS, DSL.name("CONSTRAINT_45"), new TableField[] { Links.LINKS.LINK_URI }, true);
    public static final UniqueKey<SubscriptionsRecord> CONSTRAINT_3 = Internal.createUniqueKey(Subscriptions.SUBSCRIPTIONS, DSL.name("CONSTRAINT_3"), new TableField[] { Subscriptions.SUBSCRIPTIONS.CHAT_ID, Subscriptions.SUBSCRIPTIONS.LINK_ID }, true);
    public static final UniqueKey<TgchatsRecord> CONSTRAINT_D = Internal.createUniqueKey(Tgchats.TGCHATS, DSL.name("CONSTRAINT_D"), new TableField[] { Tgchats.TGCHATS.ID }, true);

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<SubscriptionsRecord, TgchatsRecord> FK_CHATS = Internal.createForeignKey(Subscriptions.SUBSCRIPTIONS, DSL.name("FK_CHATS"), new TableField[] { Subscriptions.SUBSCRIPTIONS.CHAT_ID }, Keys.CONSTRAINT_D, new TableField[] { Tgchats.TGCHATS.ID }, true);
    public static final ForeignKey<SubscriptionsRecord, LinksRecord> FK_LINKS = Internal.createForeignKey(Subscriptions.SUBSCRIPTIONS, DSL.name("FK_LINKS"), new TableField[] { Subscriptions.SUBSCRIPTIONS.LINK_ID }, Keys.CONSTRAINT_4, new TableField[] { Links.LINKS.ID }, true);
}
