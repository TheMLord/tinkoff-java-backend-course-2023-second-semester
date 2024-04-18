/*
 * This file is generated by jOOQ.
 */
package edu.java.domain.jooq.tables;


import edu.java.domain.jooq.DefaultSchema;
import edu.java.domain.jooq.Keys;
import edu.java.domain.jooq.tables.records.TgchatsRecord;

import java.time.OffsetDateTime;
import java.util.function.Function;

import javax.annotation.processing.Generated;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function3;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row3;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


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
public class Tgchats extends TableImpl<TgchatsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>TGCHATS</code>
     */
    public static final Tgchats TGCHATS = new Tgchats();

    /**
     * The class holding records for this type
     */
    @Override
    @NotNull
    public Class<TgchatsRecord> getRecordType() {
        return TgchatsRecord.class;
    }

    /**
     * The column <code>TGCHATS.ID</code>.
     */
    public final TableField<TgchatsRecord, Long> ID = createField(DSL.name("ID"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>TGCHATS.CREATED_AT</code>.
     */
    public final TableField<TgchatsRecord, OffsetDateTime> CREATED_AT = createField(DSL.name("CREATED_AT"), SQLDataType.TIMESTAMPWITHTIMEZONE(6).nullable(false), this, "");

    /**
     * The column <code>TGCHATS.CREATED_BY</code>.
     */
    public final TableField<TgchatsRecord, String> CREATED_BY = createField(DSL.name("CREATED_BY"), SQLDataType.VARCHAR(1000000000).nullable(false).defaultValue(DSL.field(DSL.raw("'themlord'"), SQLDataType.VARCHAR)), this, "");

    private Tgchats(Name alias, Table<TgchatsRecord> aliased) {
        this(alias, aliased, null);
    }

    private Tgchats(Name alias, Table<TgchatsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>TGCHATS</code> table reference
     */
    public Tgchats(String alias) {
        this(DSL.name(alias), TGCHATS);
    }

    /**
     * Create an aliased <code>TGCHATS</code> table reference
     */
    public Tgchats(Name alias) {
        this(alias, TGCHATS);
    }

    /**
     * Create a <code>TGCHATS</code> table reference
     */
    public Tgchats() {
        this(DSL.name("TGCHATS"), null);
    }

    public <O extends Record> Tgchats(Table<O> child, ForeignKey<O, TgchatsRecord> key) {
        super(child, key, TGCHATS);
    }

    @Override
    @Nullable
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    @NotNull
    public UniqueKey<TgchatsRecord> getPrimaryKey() {
        return Keys.CONSTRAINT_D;
    }

    @Override
    @NotNull
    public Tgchats as(String alias) {
        return new Tgchats(DSL.name(alias), this);
    }

    @Override
    @NotNull
    public Tgchats as(Name alias) {
        return new Tgchats(alias, this);
    }

    @Override
    @NotNull
    public Tgchats as(Table<?> alias) {
        return new Tgchats(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    @NotNull
    public Tgchats rename(String name) {
        return new Tgchats(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    @NotNull
    public Tgchats rename(Name name) {
        return new Tgchats(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    @NotNull
    public Tgchats rename(Table<?> name) {
        return new Tgchats(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    @NotNull
    public Row3<Long, OffsetDateTime, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function3<? super Long, ? super OffsetDateTime, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function3<? super Long, ? super OffsetDateTime, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
