package de.workshops.dvdshack.application.exception;

public class ActorValidationException extends RuntimeException {
    
    public ActorValidationException(String message) {
        super(message);
    }
    
    public ActorValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}