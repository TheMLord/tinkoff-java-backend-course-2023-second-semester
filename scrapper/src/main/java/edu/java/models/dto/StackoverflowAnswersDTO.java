package edu.java.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record StackoverflowAnswersDTO(List<ItemsAnswers> items) {
    public record ItemsAnswers(AnswerOwner owner, @JsonProperty("body") String body) {
        public record AnswerOwner(@JsonProperty("reputation") Long reputation,
                                  @JsonProperty("display_name") String name) {

        }
    }
}
