package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import java.util.List;

public interface CrudDao<T> {

    T insert(T object);

    T select(Object id);

    T update(T object);

    boolean delete(Object id);

    List<T> findAll();

}
