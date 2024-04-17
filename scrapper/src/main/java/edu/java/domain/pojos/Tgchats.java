package edu.java.domain.pojos;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.annotation.processing.Generated;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@SuppressWarnings({"all", "unchecked", "rawtypes", "this-escape"})
@NoArgsConstructor
@Data
public class Tgchats implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private OffsetDateTime createdAt;
    private String createdBy;

    public Tgchats(Tgchats value) {
        this.id = value.id;
        this.createdAt = value.createdAt;
        this.createdBy = value.createdBy;
    }

    @ConstructorProperties({"id", "createdAt", "createdBy"})
    public Tgchats(
        @NotNull Long id,
        @NotNull OffsetDateTime createdAt,
        @Nullable String createdBy
    ) {
        this.id = id;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }
}