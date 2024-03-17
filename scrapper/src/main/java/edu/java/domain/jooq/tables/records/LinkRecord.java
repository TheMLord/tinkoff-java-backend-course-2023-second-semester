package edu.java.domain.jooq.tables.records;

import edu.java.domain.jooq.tables.Link;
import jakarta.validation.constraints.Size;
import java.beans.ConstructorProperties;
import java.time.OffsetDateTime;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
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
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class LinkRecord extends UpdatableRecordImpl<LinkRecord> implements Record6<Long, String, OffsetDateTime, String, String, OffsetDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>LINK.ID</code>.
     */
    public void setId(@Nullable Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>LINK.ID</code>.
     */
    @Nullable
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>LINK.LINK_NAME</code>.
     */
    public void setLinkName(@NotNull String value) {
        set(1, value);
    }

    /**
     * Getter for <code>LINK.LINK_NAME</code>.
     */
    @jakarta.validation.constraints.NotNull
    @Size(max = 1000000000)
    @NotNull
    public String getLinkName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>LINK.CREATED_AT</code>.
     */
    public void setCreatedAt(@Nullable OffsetDateTime value) {
        set(2, value);
    }

    /**
     * Getter for <code>LINK.CREATED_AT</code>.
     */
    @Nullable
    public OffsetDateTime getCreatedAt() {
        return (OffsetDateTime) get(2);
    }

    /**
     * Setter for <code>LINK.CREATED_BY</code>.
     */
    public void setCreatedBy(@Nullable String value) {
        set(3, value);
    }

    /**
     * Getter for <code>LINK.CREATED_BY</code>.
     */
    @Size(max = 1000000000)
    @Nullable
    public String getCreatedBy() {
        return (String) get(3);
    }

    /**
     * Setter for <code>LINK.CONTENT</code>.
     */
    public void setContent(@Nullable String value) {
        set(4, value);
    }

    /**
     * Getter for <code>LINK.CONTENT</code>.
     */
    @Size(max = 1000000000)
    @Nullable
    public String getContent() {
        return (String) get(4);
    }

    /**
     * Setter for <code>LINK.LAST_MODIFYING</code>.
     */
    public void setLastModifying(@Nullable OffsetDateTime value) {
        set(5, value);
    }

    /**
     * Getter for <code>LINK.LAST_MODIFYING</code>.
     */
    @Nullable
    public OffsetDateTime getLastModifying() {
        return (OffsetDateTime) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    @NotNull
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    @Override
    @NotNull
    public Row6<Long, String, OffsetDateTime, String, String, OffsetDateTime> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    @NotNull
    public Row6<Long, String, OffsetDateTime, String, String, OffsetDateTime> valuesRow() {
        return (Row6) super.valuesRow();
    }

    @Override
    @NotNull
    public Field<Long> field1() {
        return Link.LINK.ID;
    }

    @Override
    @NotNull
    public Field<String> field2() {
        return Link.LINK.LINK_NAME;
    }

    @Override
    @NotNull
    public Field<OffsetDateTime> field3() {
        return Link.LINK.CREATED_AT;
    }

    @Override
    @NotNull
    public Field<String> field4() {
        return Link.LINK.CREATED_BY;
    }

    @Override
    @NotNull
    public Field<String> field5() {
        return Link.LINK.CONTENT;
    }

    @Override
    @NotNull
    public Field<OffsetDateTime> field6() {
        return Link.LINK.LAST_MODIFYING;
    }

    @Override
    @Nullable
    public Long component1() {
        return getId();
    }

    @Override
    @NotNull
    public String component2() {
        return getLinkName();
    }

    @Override
    @Nullable
    public OffsetDateTime component3() {
        return getCreatedAt();
    }

    @Override
    @Nullable
    public String component4() {
        return getCreatedBy();
    }

    @Override
    @Nullable
    public String component5() {
        return getContent();
    }

    @Override
    @Nullable
    public OffsetDateTime component6() {
        return getLastModifying();
    }

    @Override
    @Nullable
    public Long value1() {
        return getId();
    }

    @Override
    @NotNull
    public String value2() {
        return getLinkName();
    }

    @Override
    @Nullable
    public OffsetDateTime value3() {
        return getCreatedAt();
    }

    @Override
    @Nullable
    public String value4() {
        return getCreatedBy();
    }

    @Override
    @Nullable
    public String value5() {
        return getContent();
    }

    @Override
    @Nullable
    public OffsetDateTime value6() {
        return getLastModifying();
    }

    @Override
    @NotNull
    public LinkRecord value1(@Nullable Long value) {
        setId(value);
        return this;
    }

    @Override
    @NotNull
    public LinkRecord value2(@NotNull String value) {
        setLinkName(value);
        return this;
    }

    @Override
    @NotNull
    public LinkRecord value3(@Nullable OffsetDateTime value) {
        setCreatedAt(value);
        return this;
    }

    @Override
    @NotNull
    public LinkRecord value4(@Nullable String value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    @NotNull
    public LinkRecord value5(@Nullable String value) {
        setContent(value);
        return this;
    }

    @Override
    @NotNull
    public LinkRecord value6(@Nullable OffsetDateTime value) {
        setLastModifying(value);
        return this;
    }

    @Override
    @NotNull
    public LinkRecord values(@Nullable Long value1, @NotNull String value2, @Nullable OffsetDateTime value3, @Nullable String value4, @Nullable String value5, @Nullable OffsetDateTime value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached LinkRecord
     */
    public LinkRecord() {
        super(Link.LINK);
    }

    /**
     * Create a detached, initialised LinkRecord
     */
    @ConstructorProperties({ "id", "linkName", "createdAt", "createdBy", "content", "lastModifying" })
    public LinkRecord(@Nullable Long id, @NotNull String linkName, @Nullable OffsetDateTime createdAt, @Nullable String createdBy, @Nullable String content, @Nullable OffsetDateTime lastModifying) {
        super(Link.LINK);

        setId(id);
        setLinkName(linkName);
        setCreatedAt(createdAt);
        setCreatedBy(createdBy);
        setContent(content);
        setLastModifying(lastModifying);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised LinkRecord
     */
    public LinkRecord(edu.java.domain.jooq.tables.pojos.Link value) {
        super(Link.LINK);

        if (value != null) {
            setId(value.getId());
            setLinkName(value.getLinkName());
            setCreatedAt(value.getCreatedAt());
            setCreatedBy(value.getCreatedBy());
            setContent(value.getContent());
            setLastModifying(value.getLastModifying());
            resetChangedOnNotNull();
        }
    }
}