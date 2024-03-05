package edu.java.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RemoveUserException extends RuntimeException {
    public RemoveUserException(Exception e) {
        super(e);
    }
}
