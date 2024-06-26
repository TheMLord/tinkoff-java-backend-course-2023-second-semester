package edu.java.domain.jooq.tables.records;

import edu.java.domain.jooq.tables.Links;
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
@SuppressWarnings({"all", "unchecked", "rawtypes", "this-escape"})
public class LinksRecord extends UpdatableRecordImpl<LinksRecord>
    implements Record6<Long, String, OffsetDateTime, String, String, OffsetDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>LINKS.ID</code>.
     */
    public void setId(@Nullable Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>LINKS.ID</code>.
     */
    @Nullable
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>LINKS.LINK_URI</code>.
     */
    public void setLinkUri(@NotNull String value) {
        set(1, value);
    }

    /**
     * Getter for <code>LINKS.LINK_URI</code>.
     */
    @jakarta.validation.constraints.NotNull
    @Size(max = 1000000000)
    @NotNull
    public String getLinkUri() {
        return (String) get(1);
    }

    /**
     * Setter for <code>LINKS.CREATED_AT</code>.
     */
    public void setCreatedAt(@NotNull OffsetDateTime value) {
        set(2, value);
    }

    /**
     * Getter for <code>LINKS.CREATED_AT</code>.
     */
    @jakarta.validation.constraints.NotNull
    @NotNull
    public OffsetDateTime getCreatedAt() {
        return (OffsetDateTime) get(2);
    }

    /**
     * Setter for <code>LINKS.CREATED_BY</code>.
     */
    public void setCreatedBy(@Nullable String value) {
        set(3, value);
    }

    /**
     * Getter for <code>LINKS.CREATED_BY</code>.
     */
    @Size(max = 1000000000)
    @Nullable
    public String getCreatedBy() {
        return (String) get(3);
    }

    /**
     * Setter for <code>LINKS.CONTENT</code>.
     */
    public void setContent(@Nullable String value) {
        set(4, value);
    }

    /**
     * Getter for <code>LINKS.CONTENT</code>.
     */
    @Size(max = 1000000000)
    @Nullable
    public String getContent() {
        return (String) get(4);
    }

    /**
     * Setter for <code>LINKS.LAST_MODIFYING</code>.
     */
    public void setLastModifying(@Nullable OffsetDateTime value) {
        set(5, value);
    }

    /**
     * Getter for <code>LINKS.LAST_MODIFYING</code>.
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
        return Links.LINKS.ID;
    }

    @Override
    @NotNull
    public Field<String> field2() {
        return Links.LINKS.LINK_URI;
    }

    @Override
    @NotNull
    public Field<OffsetDateTime> field3() {
        return Links.LINKS.CREATED_AT;
    }

    @Override
    @NotNull
    public Field<String> field4() {
        return Links.LINKS.CREATED_BY;
    }

    @Override
    @NotNull
    public Field<String> field5() {
        return Links.LINKS.CONTENT;
    }

    @Override
    @NotNull
    public Field<OffsetDateTime> field6() {
        return Links.LINKS.LAST_MODIFYING;
    }

    @Override
    @Nullable
    public Long component1() {
        return getId();
    }

    @Override
    @NotNull
    public String component2() {
        return getLinkUri();
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
        return getLinkUri();
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
    public LinksRecord value1(@Nullable Long value) {
        setId(value);
        return this;
    }

    @Override
    @NotNull
    public LinksRecord value2(@NotNull String value) {
        setLinkUri(value);
        return this;
    }

    @Override
    @NotNull
    public LinksRecord value3(@NotNull OffsetDateTime value) {
        setCreatedAt(value);
        return this;
    }

    @Override
    @NotNull
    public LinksRecord value4(@Nullable String value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    @NotNull
    public LinksRecord value5(@Nullable String value) {
        setContent(value);
        return this;
    }

    @Override
    @NotNull
    public LinksRecord value6(@Nullable OffsetDateTime value) {
        setLastModifying(value);
        return this;
    }

    @Override
    @NotNull
    public LinksRecord values(
        @Nullable Long value1,
        @NotNull String value2,
        @NotNull OffsetDateTime value3,
        @Nullable String value4,
        @Nullable String value5,
        @Nullable OffsetDateTime value6
    ) {
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
     * Create a detached LinksRecord
     */
    public LinksRecord() {
        super(Links.LINKS);
    }

    /**
     * Create a detached, initialised LinksRecord
     */
    @ConstructorProperties({"id", "linkUri", "createdAt", "createdBy", "content", "lastModifying"})
    public LinksRecord(
        @Nullable Long id,
        @NotNull String linkUri,
        @NotNull OffsetDateTime createdAt,
        @Nullable String createdBy,
        @Nullable String content,
        @Nullable OffsetDateTime lastModifying
    ) {
        super(Links.LINKS);

        setId(id);
        setLinkUri(linkUri);
        setCreatedAt(createdAt);
        setCreatedBy(createdBy);
        setContent(content);
        setLastModifying(lastModifying);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised LinksRecord
     */
    public LinksRecord(edu.java.domain.jooq.pojos.Links value) {
        super(Links.LINKS);

        if (value != null) {
            setId(value.getId());
            setLinkUri(value.getLinkUri());
            setCreatedAt(value.getCreatedAt());
            setCreatedBy(value.getCreatedBy());
            setContent(value.getContent());
            setLastModifying(value.getLastModifying());
            resetChangedOnNotNull();
        }
    }
}
