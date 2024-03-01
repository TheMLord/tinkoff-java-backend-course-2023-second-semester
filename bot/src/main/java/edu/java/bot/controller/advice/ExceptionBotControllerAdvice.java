package edu.java.bot.controller.advice;

import edu.java.bot.controller.dto.ApiErrorResponse;
import edu.java.bot.exceptions.InvalidUpdateException;
import java.util.Arrays;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ExceptionBotControllerAdvice {

    @ExceptionHandler(InvalidUpdateException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> exceptionInvalidUpdate(InvalidUpdateException e) {
        return Mono.just(
            ResponseEntity
                .badRequest()
                .body(ApiErrorResponse
                    .builder()
                    .description("Некорректные параметры запроса")
                    .code("400")
                    .exceptionMessage(InvalidUpdateException.class.getName())
                    .exceptionMessage(e.getMessage())
                    .stacktrace(Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList())
                    .build()
                )
        );
    }

}
