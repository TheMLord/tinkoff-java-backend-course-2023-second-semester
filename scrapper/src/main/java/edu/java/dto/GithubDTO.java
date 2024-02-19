package edu.java.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record GithubDTO(@NotNull GithubOwner owner,
                        @JsonProperty("created_at") OffsetDateTime createdAt,
                        @JsonProperty("updated_at") OffsetDateTime updatedAt,
                        @JsonProperty("pushed_at") OffsetDateTime pushedAt) {
    public record GithubOwner(String login, long id) {
    }
}
