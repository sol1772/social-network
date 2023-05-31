package com.getjavajob.training.maksyutovs.socialnetwork.dao;

public interface CrudDao<T, V> {

    T insert(String query, T type) throws DaoException;

    T select(String query, String field, V value) throws DaoRuntimeException;

    T update(String query, String field, V value, T type) throws DaoException;

    T delete(String query, T type) throws DaoException;

}
