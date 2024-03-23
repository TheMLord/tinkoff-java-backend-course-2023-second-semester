package edu.java.models.pojo;

import edu.java.models.dto.StackoverflowAnswersDTO;
import edu.java.models.dto.StackoverflowQuestionDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class StackoverflowContent {
    private StackoverflowQuestionDTO stackoverflowQuestionDTO;
    private StackoverflowAnswersDTO stackoverflowAnswersDTO;
}
