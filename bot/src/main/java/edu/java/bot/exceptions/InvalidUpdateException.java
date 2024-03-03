package edu.java.bot.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidUpdateException extends RuntimeException {
    public InvalidUpdateException(Exception e) {
        super(e);
    }
}
