package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.MessageType;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.annotations.QueryHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

@Repository
public class AccountDao extends AbstractCrudDao<Account> {

    private static final Logger logger = LoggerFactory.getLogger(AccountDao.class);

    public AccountDao() {
        super(Account.class);
    }

    private static Session getSession(EntityManager em) {
        return em.unwrap(Session.class);
    }

    public Account selectByEmail(String email) {
        Session session = getSession(em);
        session.setDefaultReadOnly(true);
        return session.bySimpleNaturalId(Account.class).load(email);
    }

    public Account selectById(int id) {
        Account account = null;
        String q = "SELECT DISTINCT a FROM Account a LEFT JOIN FETCH a.phones WHERE a.id=:id";
        try {
            account = em.createQuery(q, Account.class)
                    .setParameter("id", id)
                    .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
                    .getSingleResult();
            Hibernate.initialize(account.getAddresses());
            Hibernate.initialize(account.getMessengers());
            return account;
        } catch (NoResultException e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage());
            }
            return account;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Account> selectTargetAccounts(Account account, MessageType type) {
        String q;
        if (type == null) {
            q = "SELECT a.* FROM Account a INNER JOIN Message m ON a.id = m.trgId" +
                    " WHERE m.accId=:id UNION " +
                    " SELECT a.* FROM Account a INNER JOIN Message m ON a.id = m.accId" +
                    " WHERE m.trgId=:id GROUP BY a.id;";
        } else {
            q = "SELECT a.* FROM Account a INNER JOIN Message m ON a.id = m.trgId" +
                    " WHERE m.accId=:id AND m.msgType=:type UNION " +
                    " SELECT a.* FROM Account a INNER JOIN Message m ON a.id = m.accId" +
                    " WHERE m.trgId=:id AND m.msgType=:type GROUP BY a.id;";
        }
        if (type == null) {
            return em.createNativeQuery(q, Account.class).setParameter("id", account.getId()).getResultList();
        } else {
            return em.createNativeQuery(q, Account.class)
                    .setParameter("id", account.getId()).setParameter("type", type.toString()).getResultList();
        }
    }

}