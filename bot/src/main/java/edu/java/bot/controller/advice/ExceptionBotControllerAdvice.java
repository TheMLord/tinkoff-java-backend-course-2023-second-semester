package edu.java.bot.controller.advice;

import edu.java.bot.exceptions.InvalidUpdateException;
import edu.java.bot.models.dto.api.response.ApiErrorResponse;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.resource.NoResourceFoundException;

/**
 * Exception interceptor class that occurs on REST controllers.
 */
@RestControllerAdvice
public class ExceptionBotControllerAdvice {
    private static final String INTERNAL_SERVER_ERROR_HTTP_CODE = "500";
    private static final String BAD_GATEWAY_HTTP_CODE = "502";
    private static final String BAD_REQUEST_HTTP_CODE = "400";
    private static final String NOT_ACCEPTABLE_HTTP_CODE = "406";

    public static final String ERROR_SEND_UPDATE_DESCRIPTION = "Ошибка доставки обновления";
    public static final String SERVER_ERROR_DESCRIPTION = "Ошибка на стороне сервера";
    public static final String INCORRECT_REQUEST_PARAM_DESCRIPTION = "Некорректные параметры запроса";
    public static final String UNSUPPORTED_REQUEST_DESCRIPTION = "Неподдерживаемый запрос";

    @ExceptionHandler(InvalidUpdateException.class)
    public ResponseEntity<ApiErrorResponse> exceptionInvalidUpdate(InvalidUpdateException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_ACCEPTABLE)
            .body(ApiErrorResponse
                .builder()
                .description(ERROR_SEND_UPDATE_DESCRIPTION)
                .code(NOT_ACCEPTABLE_HTTP_CODE)
                .exceptionMessage(e.getFailedUpdateInfo())
                .stacktrace(Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList())
                .build()
            );
    }

    /**
     * Method that intercepts an exception that occurs when a request with incorrect arguments is received.
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> notSupportedContentType(HttpMediaTypeNotSupportedException e) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiErrorResponse.builder()
                .description(INCORRECT_REQUEST_PARAM_DESCRIPTION)
                .code(BAD_REQUEST_HTTP_CODE)
                .exceptionName(e.getClass().getName())
                .exceptionMessage(e.getMessage())
                .stacktrace(Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList())
                .build()
            );
    }

    /**
     * Method that intercepts an exception that occurs when receiving unsupported requests.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> notSupportedRequestException(NoResourceFoundException e) {
        return ResponseEntity
            .status(HttpStatus.BAD_GATEWAY)
            .body(ApiErrorResponse.builder()
                .description(UNSUPPORTED_REQUEST_DESCRIPTION)
                .code(BAD_GATEWAY_HTTP_CODE)
                .exceptionName(e.getClass().getName())
                .exceptionMessage(e.getMessage())
                .stacktrace(Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList())
                .build()
            );
    }

    /**
     * Method for intercepting other non-specialized exceptions to wrap them in an ApiErrorResponse.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> otherExceptions(Exception e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiErrorResponse.builder()
                .description(SERVER_ERROR_DESCRIPTION)
                .code(INTERNAL_SERVER_ERROR_HTTP_CODE)
                .exceptionName(e.getClass().getName())
                .exceptionMessage(e.getMessage())
                .stacktrace(Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList())
                .build()
            );
    }
}
