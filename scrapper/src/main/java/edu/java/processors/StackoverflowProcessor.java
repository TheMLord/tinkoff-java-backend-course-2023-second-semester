package edu.java.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.models.dto.StackoverflowAnswersDTO;
import edu.java.models.pojo.LinkChanges;
import edu.java.models.pojo.StackoverflowContent;
import edu.java.models.pojo.StackoverflowUriArg;
import edu.java.proxies.StackoverflowProxy;
import java.net.URI;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import reactor.core.publisher.Mono;

public class StackoverflowProcessor extends UriProcessor {
    private final StackoverflowProxy stackoverflowProxy;
    private final ObjectMapper objectMapper;

    private static final String REMOVE_PHRASE = "Deleted ";
    private static final String ADDED_PHRASE = "Added ";
    private static final String ANSWERS_PHRASE = " answer(s):\n";
    private static final String LINE_SEPARATOR = "\n";
    private static final String AUTHOR_INFO_PHRASE = "Author: %s (reputations: %d)\n%s";

    public StackoverflowProcessor(
        UriProcessor nextProcessor,
        StackoverflowProxy stackoverflowProxy,
        ObjectMapper objectMapper
    ) {
        super(nextProcessor);
        this.stackoverflowProxy = stackoverflowProxy;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean isProcessingUri(URI uri) {
        return uri.getHost().equals("stackoverflow.com");
    }

    @Override
    protected Object prepareLinkContent(Object apiArgs) {
        var stackoverflowApiArgs = (StackoverflowUriArg) apiArgs;
        var questionInfo = stackoverflowProxy
            .getQuestionRequest(stackoverflowApiArgs.questionId()).block();
        var answersInfo = stackoverflowProxy
            .getAnswersForQuestion(stackoverflowApiArgs.questionId()).block();

        return StackoverflowContent
            .builder()
            .stackoverflowQuestionDTO(questionInfo)
            .stackoverflowAnswersDTO(answersInfo)
            .build();
    }

    @Override
    protected Object parseUriArgs(String[] uriPaths) {
        return new StackoverflowUriArg(uriPaths[2]);
    }

    @SneakyThrows
    @Override
    protected Mono<LinkChanges> prepareUpdate(URI nameLink, String prevContent) {
        var prevDto = objectMapper.readValue(prevContent, StackoverflowContent.class);
        var newDto = (StackoverflowContent) processUri(nameLink);

        if (!isChangedAnswers(
            prevDto.getStackoverflowAnswersDTO(),
            newDto.getStackoverflowAnswersDTO()
        )) {
            return Mono.just(
                new LinkChanges(
                    nameLink,
                    prepareChangesDescription(prevDto, newDto),
                    objectMapper.writeValueAsString(newDto)
                )
            );
        }
        return Mono.empty();
    }

    private boolean isChangedAnswers(
        StackoverflowAnswersDTO prevAnswers,
        StackoverflowAnswersDTO newAnswers
    ) {
        return prevAnswers.equals(newAnswers);
    }

    private String prepareChangesDescription(
        StackoverflowContent prevContent,
        StackoverflowContent newContent
    ) {
        var prevAnswers = prevContent.getStackoverflowAnswersDTO().items();
        var newAnswers = newContent.getStackoverflowAnswersDTO().items();

        var deletedAnswers = prevAnswers.stream()
            .filter(answer -> !newAnswers.contains(answer))
            .map(answer -> formatAnswer(
                answer.owner().name(),
                answer.owner().reputation(),
                answer.body()
            ))
            .collect(Collectors.toList());

        var addedAnswers = newAnswers.stream()
            .filter(answer -> !prevAnswers.contains(answer))
            .map(answer -> formatAnswer(
                answer.owner().name(),
                answer.owner().reputation(),
                answer.body()
            ))
            .collect(Collectors.toList());

        var changesDescription = new StringBuilder();

        if (!deletedAnswers.isEmpty()) {
            changesDescription.append(REMOVE_PHRASE).append(deletedAnswers.size()).append(ANSWERS_PHRASE);
            changesDescription.append(String.join(LINE_SEPARATOR, deletedAnswers));
            changesDescription.append(LINE_SEPARATOR);
        }

        if (!addedAnswers.isEmpty()) {
            changesDescription.append(ADDED_PHRASE).append(addedAnswers.size()).append(ANSWERS_PHRASE);
            changesDescription.append(String.join(LINE_SEPARATOR, addedAnswers));
        }

        return changesDescription.toString();
    }

    private String formatAnswer(String name, Long reputation, String body) {
        return String.format(AUTHOR_INFO_PHRASE, name, reputation, body);
    }
}
