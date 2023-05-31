package com.getjavajob.training.maksyutovs.socialnetwork.dao;

/**
 * The class DaoException is a checked exception mainly used for forwarding to transactional methods
 */
public class DaoException extends Exception {

    public DaoException() {
    }

    public DaoException(Throwable cause) {
        super(cause);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

}
