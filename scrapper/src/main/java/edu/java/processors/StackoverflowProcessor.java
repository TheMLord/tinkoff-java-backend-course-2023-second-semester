package edu.java.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.models.dto.StackoverflowAnswersDTO;
import edu.java.models.pojo.LinkChanges;
import edu.java.models.pojo.StackoverflowContent;
import edu.java.models.pojo.StackoverflowUriArg;
import edu.java.proxies.StackoverflowProxy;
import java.net.URI;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.SneakyThrows;

public class StackoverflowProcessor extends UriProcessor {
    private final StackoverflowProxy stackoverflowProxy;
    private final ObjectMapper objectMapper;

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

        var s = stackoverflowProxy.getAnswersForQuestion(stackoverflowApiArgs.questionId()).block();
        System.out.println(s);

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
    protected Optional<LinkChanges> prepareUpdate(URI nameLink, String prevContent) {
        var prevDto = objectMapper.readValue(prevContent, StackoverflowContent.class);
        var newDto = (StackoverflowContent) processUri(nameLink);

        if (!isChangedAnswers(
            prevDto.getStackoverflowAnswersDTO(),
            newDto.getStackoverflowAnswersDTO()
        )) {
            return Optional.of(
                new LinkChanges(
                    nameLink,
                    prepareChangesDescription(prevDto, newDto),
                    objectMapper.writeValueAsString(newDto)
                )
            );
        }
        return Optional.empty();
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
            changesDescription.append("Удалено ").append(deletedAnswers.size()).append(" ответов:\n");
            changesDescription.append(String.join("\n", deletedAnswers));
            changesDescription.append("\n");
        }

        if (!addedAnswers.isEmpty()) {
            changesDescription.append("Добавлено ").append(addedAnswers.size()).append(" ответов:\n");
            changesDescription.append(String.join("\n", addedAnswers));
        }

        return changesDescription.toString();
    }

    private String formatAnswer(String name, Long reputation, String body) {
        return String.format("Автор: %s (репутация: %d)\n%s", name, reputation, body);
    }
}
