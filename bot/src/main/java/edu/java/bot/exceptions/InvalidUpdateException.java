package edu.java.bot.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class InvalidUpdateException extends RuntimeException {
    private final String failedUpdateInfo;
}
