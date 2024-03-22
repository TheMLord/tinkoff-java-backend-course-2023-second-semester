package edu.java.controller.advice;

import edu.java.exceptions.AlreadyTrackLinkException;
import edu.java.exceptions.DoubleRegistrationException;
import edu.java.exceptions.NotExistLinkException;
import edu.java.exceptions.NotExistTgChatException;
import edu.java.exceptions.NotTrackLinkException;
import edu.java.models.dto.api.response.ApiErrorResponse;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.resource.NoResourceFoundException;

@RestControllerAdvice
@Slf4j
public class ExceptionScrapperControllerAdvice {
    private static final String BAD_REQUEST_HTTP_CODE = "400";
    private static final String UNAUTHORIZED_HTTP_CODE = "401";
    private static final String FORBIDDEN_HTTP_CODE = "403";
    private static final String NOT_FOUND_HTTP_CODE = "404";
    private static final String NOT_ACCEPTABLE_HTTP_CODE = "406";
    private static final String PRECONDITION_FAILED_HTTP_CODE = "412";
    private static final String INTERNAL_SERVER_ERROR_HTTP_CODE = "500";
    private static final String BAD_GATEWAY_HTTP_CODE = "502";

    public static final String CHAT_ALREADY_REGISTER_DESCRIPTION = "Чат уже зарегистрирован";
    public static final String CHAT_NOT_REGISTER_DESCRIPTION = "Чат не зарегистрирован";

    public static final String LINK_ALREADY_TRACKED_DESCRIPTION = "Ссылка уже отслеживается";
    public static final String LINK_IS_NOT_TRACK_DESCRIPTION = "Ссылка не отслеживается чатом";

    public static final String LINK_NOT_FOUND_DESCRIPTION = "Несуществующая ссылка";

    public static final String SERVER_ERROR_DESCRIPTION = "Ошибка на стороне сервера";
    public static final String UNCORRECT_REQUEST_PARAM_DESCRIPTION = "Некорректные параметры запроса";
    public static final String UNSUPPORTED_REQUEST_DESCRIPTION = "Неподдерживаемый запрос";

    @ExceptionHandler(DoubleRegistrationException.class)
    public ResponseEntity<ApiErrorResponse> exceptionDoubleRegistration(DoubleRegistrationException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_ACCEPTABLE)
            .body(ApiErrorResponse.builder()
                .description(CHAT_ALREADY_REGISTER_DESCRIPTION)
                .code(NOT_ACCEPTABLE_HTTP_CODE)
                .exceptionName(DoubleRegistrationException.class.getName())
                .exceptionMessage(CHAT_ALREADY_REGISTER_DESCRIPTION)
                .stacktrace(Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList())
                .build()
            );
    }

    @ExceptionHandler(NotExistTgChatException.class)
    public ResponseEntity<ApiErrorResponse> exceptionNotExistChat(NotExistTgChatException e) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiErrorResponse.builder()
                .description(CHAT_NOT_REGISTER_DESCRIPTION)

                .code(UNAUTHORIZED_HTTP_CODE)
                .exceptionName(NotExistTgChatException.class.getName())
                .exceptionMessage(CHAT_NOT_REGISTER_DESCRIPTION)
                .stacktrace(Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList())
                .build());
    }

    @ExceptionHandler(AlreadyTrackLinkException.class)
    public ResponseEntity<ApiErrorResponse> exceptionAlreadyTrackLink(AlreadyTrackLinkException e) {
        return ResponseEntity
            .status(HttpStatus.PRECONDITION_FAILED)
            .body(ApiErrorResponse.builder()
                .description(LINK_ALREADY_TRACKED_DESCRIPTION)
                .code(PRECONDITION_FAILED_HTTP_CODE)
                .exceptionName(AlreadyTrackLinkException.class.getName())
                .exceptionMessage(LINK_ALREADY_TRACKED_DESCRIPTION)
                .stacktrace(Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList())
                .build());
    }

    @ExceptionHandler(NotTrackLinkException.class)
    public ResponseEntity<ApiErrorResponse> exceptionNotTrackLinkException(NotTrackLinkException e) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ApiErrorResponse.builder()
                .description(LINK_IS_NOT_TRACK_DESCRIPTION)
                .code(FORBIDDEN_HTTP_CODE)
                .exceptionName(NotExistLinkException.class.getName())
                .exceptionMessage(LINK_IS_NOT_TRACK_DESCRIPTION)
                .stacktrace(Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList())
                .build());
    }

    @ExceptionHandler(NotExistLinkException.class)
    public ResponseEntity<ApiErrorResponse> notExistLinkException(NotExistLinkException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiErrorResponse.builder()
                .description(LINK_NOT_FOUND_DESCRIPTION)
                .code(NOT_FOUND_HTTP_CODE)
                .exceptionName(NotExistLinkException.class.getName())
                .exceptionMessage(LINK_NOT_FOUND_DESCRIPTION)
                .stacktrace(Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList())
                .build());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> notSupportedContentType(HttpMediaTypeNotSupportedException e) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiErrorResponse.builder()
                .description(UNCORRECT_REQUEST_PARAM_DESCRIPTION)
                .code(BAD_REQUEST_HTTP_CODE)
                .exceptionName(e.getClass().getName())
                .exceptionMessage(e.getMessage())
                .stacktrace(Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList())
                .build()
            );
    }

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
