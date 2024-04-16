package edu.java.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.models.dto.StackoverflowDTO;
import edu.java.models.pojo.LinkChanges;
import edu.java.models.pojo.StackoverflowUriArg;
import edu.java.proxies.StackoverflowProxy;
import java.net.URI;
import java.util.Objects;
import lombok.SneakyThrows;
import reactor.core.publisher.Mono;

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
        return stackoverflowProxy.getQuestionRequest(stackoverflowApiArgs.questionId()).block();
    }

    @Override
    protected Object parseUriArgs(String[] uriPaths) {
        return new StackoverflowUriArg(uriPaths[2]);
    }

    @SneakyThrows
    @Override
    protected Mono<LinkChanges> prepareUpdate(URI nameLink, String prevContent) {
        var prevDto = objectMapper.readValue(prevContent, StackoverflowDTO.class);
        var newDto = (StackoverflowDTO) processUri(nameLink);

        if (prevDto.items().getFirst().answerCount()
            != Objects.requireNonNull(newDto).items().getFirst().answerCount()) {
            return Mono.just(
                new LinkChanges(
                    nameLink,
                    "Есть изменения",
                    objectMapper.writeValueAsString(newDto)
                )
            );
        }
        return Mono.empty();
    }
}
