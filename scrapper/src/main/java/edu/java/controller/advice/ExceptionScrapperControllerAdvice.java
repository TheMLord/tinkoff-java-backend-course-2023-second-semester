package edu.java.controller.advice;

import edu.java.exceptions.AlreadyTrackLinkException;
import edu.java.exceptions.DoubleRegistrationException;
import edu.java.exceptions.NotExistLinkException;
import edu.java.exceptions.NotFoundUserException;
import edu.java.exceptions.RemoveUserException;
import edu.java.models.dto.api.response.ApiErrorResponse;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionScrapperControllerAdvice {
    private static final String NOT_FOUND_HTTP_CODE = "404";
    public static final String CHAT_ALREADY_REGISTER_DESCRIPTION = "Чат уже зарегистрирован";
    public static final String CHAT_IS_NOT_EXIST_DESCRIPTION = "Чат не существует";
    public static final String USER_NOT_FOUND_DESCRIPTION = "Пользователь не найдет";
    public static final String LINK_ALREADY_TRACKED_DESCRIPTION = "Ссылка уже отслеживается";
    public static final String LINK_NOT_FOUND_DESCRIPTION = "Ссылка не найдена";

    @ExceptionHandler(DoubleRegistrationException.class)
    public ResponseEntity<ApiErrorResponse> exceptionDoubleRegistration(DoubleRegistrationException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiErrorResponse.builder()
                .description(CHAT_ALREADY_REGISTER_DESCRIPTION)
                .code(NOT_FOUND_HTTP_CODE)
                .exceptionName(DoubleRegistrationException.class.getName())
                .exceptionMessage(e.getMessage())
                .stacktrace(Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList())
                .build()
            );
    }

    @ExceptionHandler(RemoveUserException.class)
    public ResponseEntity<ApiErrorResponse> exceptionRemoveUser(RemoveUserException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiErrorResponse.builder()
                .description(CHAT_IS_NOT_EXIST_DESCRIPTION)
                .code(NOT_FOUND_HTTP_CODE)
                .exceptionName(RemoveUserException.class.getName())
                .exceptionMessage(e.getMessage())
                .stacktrace(Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList())
                .build());

    }

    @ExceptionHandler(NotFoundUserException.class)
    public ResponseEntity<ApiErrorResponse> exceptionNotFoundUser(NotFoundUserException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiErrorResponse.builder()
                .description(USER_NOT_FOUND_DESCRIPTION)
                .code(NOT_FOUND_HTTP_CODE)
                .exceptionName(NotFoundUserException.class.getName())
                .exceptionMessage(e.getMessage())
                .stacktrace(Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList())
                .build());
    }

    @ExceptionHandler(AlreadyTrackLinkException.class)
    public ResponseEntity<ApiErrorResponse> exceptionAlreadyTrackLink(AlreadyTrackLinkException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiErrorResponse.builder()
                .description(LINK_ALREADY_TRACKED_DESCRIPTION)
                .code(NOT_FOUND_HTTP_CODE)
                .exceptionName(AlreadyTrackLinkException.class.getName())
                .exceptionMessage(e.getMessage())
                .stacktrace(Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList())
                .build());
    }

    @ExceptionHandler(NotExistLinkException.class)
    public ResponseEntity<ApiErrorResponse> exceptionNotFoundLinkToDelete(NotFoundUserException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiErrorResponse.builder()
                .description(LINK_NOT_FOUND_DESCRIPTION)
                .code(NOT_FOUND_HTTP_CODE)
                .exceptionName(NotExistLinkException.class.getName())
                .exceptionMessage(e.getMessage())
                .stacktrace(Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList())
                .build());
    }
}
