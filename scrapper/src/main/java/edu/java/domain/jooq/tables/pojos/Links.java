package edu.java.domain.jooq.tables.pojos;

import jakarta.validation.constraints.Size;
import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


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
@SuppressWarnings({"all", "unchecked", "rawtypes", "this-escape", "MemberName", "EmptyLineSeparator"})
public class Links implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String linkUri;
    private OffsetDateTime createdAt;
    private String createdBy;
    private String content;
    private OffsetDateTime lastModifying;

    public Links() {}

    public Links(Links value) {
        this.id = value.id;
        this.linkUri = value.linkUri;
        this.createdAt = value.createdAt;
        this.createdBy = value.createdBy;
        this.content = value.content;
        this.lastModifying = value.lastModifying;
    }

    @ConstructorProperties({ "id", "linkUri", "createdAt", "createdBy", "content", "lastModifying" })
    public Links(
        @Nullable Long id,
        @NotNull String linkUri,
        @NotNull OffsetDateTime createdAt,
        @Nullable String createdBy,
        @Nullable String content,
        @Nullable OffsetDateTime lastModifying
    ) {
        this.id = id;
        this.linkUri = linkUri;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.content = content;
        this.lastModifying = lastModifying;
    }

    /**
     * Getter for <code>LINKS.ID</code>.
     */
    @Nullable
    public Long getId() {
        return this.id;
    }

    /**
     * Setter for <code>LINKS.ID</code>.
     */
    public void setId(@Nullable Long id) {
        this.id = id;
    }

    /**
     * Getter for <code>LINKS.LINK_URI</code>.
     */
    @jakarta.validation.constraints.NotNull
    @Size(max = 1000000000)
    @NotNull
    public String getLinkUri() {
        return this.linkUri;
    }

    /**
     * Setter for <code>LINKS.LINK_URI</code>.
     */
    public void setLinkUri(@NotNull String linkUri) {
        this.linkUri = linkUri;
    }

    /**
     * Getter for <code>LINKS.CREATED_AT</code>.
     */
    @jakarta.validation.constraints.NotNull
    @NotNull
    public OffsetDateTime getCreatedAt() {
        return this.createdAt;
    }

    /**
     * Setter for <code>LINKS.CREATED_AT</code>.
     */
    public void setCreatedAt(@NotNull OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Getter for <code>LINKS.CREATED_BY</code>.
     */
    @Size(max = 1000000000)
    @Nullable
    public String getCreatedBy() {
        return this.createdBy;
    }

    /**
     * Setter for <code>LINKS.CREATED_BY</code>.
     */
    public void setCreatedBy(@Nullable String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Getter for <code>LINKS.CONTENT</code>.
     */
    @Size(max = 1000000000)
    @Nullable
    public String getContent() {
        return this.content;
    }

    /**
     * Setter for <code>LINKS.CONTENT</code>.
     */
    public void setContent(@Nullable String content) {
        this.content = content;
    }

    /**
     * Getter for <code>LINKS.LAST_MODIFYING</code>.
     */
    @Nullable
    public OffsetDateTime getLastModifying() {
        return this.lastModifying;
    }

    /**
     * Setter for <code>LINKS.LAST_MODIFYING</code>.
     */
    public void setLastModifying(@Nullable OffsetDateTime lastModifying) {
        this.lastModifying = lastModifying;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Links other = (Links) obj;
        if (this.id == null) {
            if (other.id != null)
                return false;
        }
        else if (!this.id.equals(other.id))
            return false;
        if (this.linkUri == null) {
            if (other.linkUri != null)
                return false;
        }
        else if (!this.linkUri.equals(other.linkUri))
            return false;
        if (this.createdAt == null) {
            if (other.createdAt != null)
                return false;
        }
        else if (!this.createdAt.equals(other.createdAt))
            return false;
        if (this.createdBy == null) {
            if (other.createdBy != null)
                return false;
        }
        else if (!this.createdBy.equals(other.createdBy))
            return false;
        if (this.content == null) {
            if (other.content != null)
                return false;
        }
        else if (!this.content.equals(other.content))
            return false;
        if (this.lastModifying == null) {
            if (other.lastModifying != null)
                return false;
        }
        else if (!this.lastModifying.equals(other.lastModifying))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.linkUri == null) ? 0 : this.linkUri.hashCode());
        result = prime * result + ((this.createdAt == null) ? 0 : this.createdAt.hashCode());
        result = prime * result + ((this.createdBy == null) ? 0 : this.createdBy.hashCode());
        result = prime * result + ((this.content == null) ? 0 : this.content.hashCode());
        result = prime * result + ((this.lastModifying == null) ? 0 : this.lastModifying.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Links (");

        sb.append(id);
        sb.append(", ").append(linkUri);
        sb.append(", ").append(createdAt);
        sb.append(", ").append(createdBy);
        sb.append(", ").append(content);
        sb.append(", ").append(lastModifying);

        sb.append(")");
        return sb.toString();
    }
}
