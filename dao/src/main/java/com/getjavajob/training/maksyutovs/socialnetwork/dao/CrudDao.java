package com.getjavajob.training.maksyutovs.socialnetwork.dao;

public interface CrudDao<T, V> {

    T insert(String query, T type);

    T select(String query, String field, V value);

    T update(String query, String field, V value, T type);

    T delete(String query, T type);

}
