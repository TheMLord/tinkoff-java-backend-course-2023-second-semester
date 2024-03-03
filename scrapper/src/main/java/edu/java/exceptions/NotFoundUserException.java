package edu.java.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NotFoundUserException extends RuntimeException {
    public NotFoundUserException(Exception e) {
        super(e);
    }
}
