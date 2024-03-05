package edu.java.bot.controller.advice;

import edu.java.bot.exceptions.InvalidUpdateException;
import edu.java.bot.models.dto.api.response.ApiErrorResponse;
import java.util.Arrays;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionBotControllerAdvice {

    @ExceptionHandler(InvalidUpdateException.class)
    public ResponseEntity<ApiErrorResponse> exceptionInvalidUpdate(InvalidUpdateException e) {
        return ResponseEntity
            .badRequest()
            .body(ApiErrorResponse
                .builder()
                .description("Некорректные параметры запроса")
                .code("400")
                .exceptionMessage(e.getMessage())
                .stacktrace(Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList())
                .build()
            );

    }

}
