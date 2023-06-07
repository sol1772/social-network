package com.getjavajob.training.maksyutovs.socialnetwork.dao;

/**
 * The class DaoException is a checked exception mainly used in business logic.
 */
public class DaoException extends Exception {

    public DaoException() {
    }

    public DaoException(String message) {
        super(message);
    }

    public DaoException(Throwable cause) {
        super(cause);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

}
