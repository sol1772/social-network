package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public abstract class AbstractCrudDao<T> implements CrudDao<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCrudDao.class);
    @PersistenceContext
    EntityManager em;
    private Class<T> tClass;

    protected AbstractCrudDao() {
    }

    protected AbstractCrudDao(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public T insert(final T object) {
        em.persist(object);
        LOGGER.info("Inserted object {}", object);
        return object;
    }

    @Override
    public T select(final Object id) {
        return em.find(tClass, id);
    }

    @Override
    public T update(final T object) {
        return em.merge(object);
    }

    @Override
    public boolean delete(final Object id) {
        try {
            em.remove(em.getReference(tClass, id));
            LOGGER.info("Deleted object of {} with id {}", tClass, id);
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return false;
        }
    }

    @Override
    public List<T> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(tClass);
        Root<T> rootEntry = cq.from(tClass);
        CriteriaQuery<T> all = cq.select(rootEntry);
        return em.createQuery(all).getResultList();
    }

}
