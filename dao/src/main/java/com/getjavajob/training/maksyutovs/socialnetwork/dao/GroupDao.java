package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Group;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.dto.Mapper;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.annotations.QueryHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class GroupDao extends AbstractCrudDao<Group> {

    private static final Logger logger = LoggerFactory.getLogger(GroupDao.class);
    private final Mapper mapper = new Mapper();

    public GroupDao() {
        super(Group.class);
    }

    public Group selectByTitle(String title) {
        Group group;
        try (Session session = em.unwrap(Session.class)) {
            group = session.bySimpleNaturalId(Group.class).load(title);
        }
        return group;
    }

    public Group selectById(int id) {
        Group group = null;
        try {
            group = em.createQuery("SELECT g FROM Group g WHERE g.id=:id", Group.class)
                    .setParameter("id", id)
                    .getSingleResult();
            Hibernate.initialize(group.getMembers());
            return group;
        } catch (NoResultException e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage());
            }
            return group;
        }
    }

    public List<Group> selectByAccount(Account account) {
        List<Group> groups = new ArrayList<>();
        String q = "SELECT DISTINCT g FROM Group g INNER JOIN FETCH g.members m WHERE m.account=:account";
        try {
            groups = em.createQuery(q, Group.class)
                    .setParameter("account", account)
                    .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
                    .getResultList();
            return groups;
        } catch (NoResultException e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage());
            }
            return groups;
        }
    }

    public List<Group> selectByString(String substring, int start, int total) {
        String searchString = "%" + substring + "%";
        String q = "SELECT g.id, g.title, g.metaTitle FROM Group g WHERE g.title LIKE :str ORDER BY g.title";
        try {
            return em.createQuery(q, Object[].class)
                    .setParameter("str", searchString)
                    .setFirstResult(total > 0 ? start - 1 : total)
                    .setMaxResults(total)
                    .getResultList().stream().map(mapper::toGroup).collect(Collectors.toList());
        } catch (NoResultException e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage());
            }
            return new ArrayList<>();
        }
    }

    public Integer selectCountByString(String substring) {
        String searchString = "%" + substring + "%";
        try {
            return em.createQuery("SELECT COUNT(*) FROM Group g WHERE g.title LIKE :str", Long.class)
                    .setParameter("str", searchString)
                    .getSingleResult().intValue();
        } catch (NoResultException e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage());
            }
            return 0;
        }
    }

}