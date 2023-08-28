package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.MessageType;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.dto.AccountDto;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.dto.Mapper;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.annotations.QueryHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class AccountDao extends AbstractCrudDao<Account> {

    private static final Logger logger = LoggerFactory.getLogger(AccountDao.class);
    private final Mapper mapper = new Mapper();

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

    public List<AccountDto> selectByString(String substring, int start, int total) {
        String searchString = "%" + substring + "%";
        String q = "SELECT a.id, a.firstName, a.lastName, a.userName, a.email FROM Account a " +
                "WHERE a.firstName LIKE :str OR a.lastName LIKE :str ORDER BY a.lastName";
        try {
            return em.createQuery(q, Object[].class)
                    .setParameter("str", searchString)
                    .setFirstResult(total > 0 ? start - 1 : total)
                    .setMaxResults(total)
                    .getResultList().stream().map(mapper::toAccountDto).collect(Collectors.toList());
        } catch (NoResultException e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage());
            }
            return new ArrayList<>();
        }
    }

    public int selectCountByString(String substring) {
        String searchString = "%" + substring + "%";
        String q = "SELECT COUNT(*) FROM Account a WHERE a.firstName LIKE :str OR a.lastName LIKE :str";
        try {
            return em.createQuery(q, Long.class)
                    .setParameter("str", searchString)
                    .getSingleResult().intValue();
        } catch (NoResultException e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage());
            }
            return 0;
        }
    }

    public boolean checkByEmail(String email) {
        return selectByEmail(email) != null;
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