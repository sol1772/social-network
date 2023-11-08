package com.getjavajob.training.maksyutovs.socialnetwork.dao;

/**
 * The class DaoRuntimeException is an unchecked exception for use in dao layer
 */
public class DaoRuntimeException extends RuntimeException {

    public DaoRuntimeException() {
    }

    public DaoRuntimeException(String message) {
        super(message);
    }

    public DaoRuntimeException(Throwable cause) {
        super(cause);
    }

    public DaoRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

}
