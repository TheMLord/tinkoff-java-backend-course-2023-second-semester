package edu.java.bot.exceptions;

public class InvalidUpdateException extends RuntimeException {
    public InvalidUpdateException(Exception e) {
        super(e);
    }
}
