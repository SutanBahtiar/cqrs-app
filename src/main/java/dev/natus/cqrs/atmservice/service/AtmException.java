package dev.natus.cqrs.atmservice.service;

public class AtmException extends RuntimeException {

    public AtmException() {
    }

    public AtmException(String message) {
        super(message);
    }

    public AtmException(String message, Throwable cause) {
        super(message, cause);
    }

    public AtmException(Throwable cause) {
        super(cause);
    }

    public AtmException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
