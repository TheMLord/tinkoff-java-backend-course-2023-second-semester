package edu.java.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record StackoverflowQuestionDTO(List<ItemResponse> items) {
    public record ItemResponse(@NotNull StackoverflowOwner owner, @JsonProperty("answer_count") int answerCount) {
        public record StackoverflowOwner(@JsonProperty("account_id") long accountId,
                                         @JsonProperty("display_name") String displayName) {
        }
    }
}
