package com.getjavajob.training.maksyutovs.socialnetwork.dao;

public interface CrudDao<T, V> {

    T insert(T type);

    T select(String field, V value);

    T update(String field, V value, T type);

    T delete(T type);

}
