package edu.java.domain.jooq.tables.records;

import edu.java.domain.jooq.tables.Subscriptions;
import jakarta.validation.constraints.Size;
import java.beans.ConstructorProperties;
import java.time.OffsetDateTime;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;

/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.7"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({"all", "unchecked", "rawtypes", "this-escape"})
public class SubscriptionsRecord extends UpdatableRecordImpl<SubscriptionsRecord>
    implements Record4<Long, Long, OffsetDateTime, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>SUBSCRIPTIONS.CHAT_ID</code>.
     */
    public void setChatId(@NotNull Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>SUBSCRIPTIONS.CHAT_ID</code>.
     */
    @jakarta.validation.constraints.NotNull
    @NotNull
    public Long getChatId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>SUBSCRIPTIONS.LINK_ID</code>.
     */
    public void setLinkId(@NotNull Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>SUBSCRIPTIONS.LINK_ID</code>.
     */
    @jakarta.validation.constraints.NotNull
    @NotNull
    public Long getLinkId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>SUBSCRIPTIONS.CREATED_AT</code>.
     */
    public void setCreatedAt(@NotNull OffsetDateTime value) {
        set(2, value);
    }

    /**
     * Getter for <code>SUBSCRIPTIONS.CREATED_AT</code>.
     */
    @jakarta.validation.constraints.NotNull
    @NotNull
    public OffsetDateTime getCreatedAt() {
        return (OffsetDateTime) get(2);
    }

    /**
     * Setter for <code>SUBSCRIPTIONS.CREATED_BY</code>.
     */
    public void setCreatedBy(@Nullable String value) {
        set(3, value);
    }

    /**
     * Getter for <code>SUBSCRIPTIONS.CREATED_BY</code>.
     */
    @Size(max = 1000000000)
    @Nullable
    public String getCreatedBy() {
        return (String) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    @NotNull
    public Record2<Long, Long> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    @NotNull
    public Row4<Long, Long, OffsetDateTime, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    @NotNull
    public Row4<Long, Long, OffsetDateTime, String> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    @NotNull
    public Field<Long> field1() {
        return Subscriptions.SUBSCRIPTIONS.CHAT_ID;
    }

    @Override
    @NotNull
    public Field<Long> field2() {
        return Subscriptions.SUBSCRIPTIONS.LINK_ID;
    }

    @Override
    @NotNull
    public Field<OffsetDateTime> field3() {
        return Subscriptions.SUBSCRIPTIONS.CREATED_AT;
    }

    @Override
    @NotNull
    public Field<String> field4() {
        return Subscriptions.SUBSCRIPTIONS.CREATED_BY;
    }

    @Override
    @NotNull
    public Long component1() {
        return getChatId();
    }

    @Override
    @NotNull
    public Long component2() {
        return getLinkId();
    }

    @Override
    @NotNull
    public OffsetDateTime component3() {
        return getCreatedAt();
    }

    @Override
    @Nullable
    public String component4() {
        return getCreatedBy();
    }

    @Override
    @NotNull
    public Long value1() {
        return getChatId();
    }

    @Override
    @NotNull
    public Long value2() {
        return getLinkId();
    }

    @Override
    @NotNull
    public OffsetDateTime value3() {
        return getCreatedAt();
    }

    @Override
    @Nullable
    public String value4() {
        return getCreatedBy();
    }

    @Override
    @NotNull
    public SubscriptionsRecord value1(@NotNull Long value) {
        setChatId(value);
        return this;
    }

    @Override
    @NotNull
    public SubscriptionsRecord value2(@NotNull Long value) {
        setLinkId(value);
        return this;
    }

    @Override
    @NotNull
    public SubscriptionsRecord value3(@NotNull OffsetDateTime value) {
        setCreatedAt(value);
        return this;
    }

    @Override
    @NotNull
    public SubscriptionsRecord value4(@Nullable String value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    @NotNull
    public SubscriptionsRecord values(
        @NotNull Long value1,
        @NotNull Long value2,
        @NotNull OffsetDateTime value3,
        @Nullable String value4
    ) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached SubscriptionsRecord
     */
    public SubscriptionsRecord() {
        super(Subscriptions.SUBSCRIPTIONS);
    }

    /**
     * Create a detached, initialised SubscriptionsRecord
     */
    @ConstructorProperties({"chatId", "linkId", "createdAt", "createdBy"})
    public SubscriptionsRecord(
        @NotNull Long chatId,
        @NotNull Long linkId,
        @NotNull OffsetDateTime createdAt,
        @Nullable String createdBy
    ) {
        super(Subscriptions.SUBSCRIPTIONS);

        setChatId(chatId);
        setLinkId(linkId);
        setCreatedAt(createdAt);
        setCreatedBy(createdBy);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised SubscriptionsRecord
     */
    public SubscriptionsRecord(edu.java.domain.jooq.pojos.Subscriptions value) {
        super(Subscriptions.SUBSCRIPTIONS);

        if (value != null) {
            setChatId(value.getChatId());
            setLinkId(value.getLinkId());
            setCreatedAt(value.getCreatedAt());
            setCreatedBy(value.getCreatedBy());
            resetChangedOnNotNull();
        }
    }
}