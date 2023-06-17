package com.getjavajob.training.maksyutovs.socialnetwork.service;

/**
 * The class ValidationRuntimeException is an unchecked exception for object validation purposes
 */
public class ValidationRuntimeException extends RuntimeException {

    public ValidationRuntimeException() {
    }

    public ValidationRuntimeException(String message) {
        super(message);
    }

    public ValidationRuntimeException(Throwable cause) {
        super(cause);
    }

    public ValidationRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

}
