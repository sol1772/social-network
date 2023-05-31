package com.getjavajob.training.maksyutovs.socialnetwork.dao;

/**
 * The class DaoRuntimeException is an unchecked exception for common use
 */
public class DaoRuntimeException extends RuntimeException {

    public DaoRuntimeException() {
    }

    public DaoRuntimeException(Throwable cause) {
        super(cause);
    }

    public DaoRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

}
