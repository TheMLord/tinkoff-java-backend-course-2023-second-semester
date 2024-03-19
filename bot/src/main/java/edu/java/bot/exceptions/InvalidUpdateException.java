package edu.java.bot.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Exception that is thrown when the delivery of the update to the chat fails.
 */
@RequiredArgsConstructor
@Getter
public class InvalidUpdateException extends RuntimeException {
    private final String failedUpdateInfo;
}
