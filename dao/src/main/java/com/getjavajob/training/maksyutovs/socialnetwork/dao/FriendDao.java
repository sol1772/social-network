package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Friend;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class FriendDao extends AbstractCrudDao<Friend> {

    private static final Logger logger = Logger.getLogger(FriendDao.class.getName());

    public FriendDao() {
        super(Friend.class);
    }

    public Friend selectFriend(Account account, Account friendAccount) {
        String q = "SELECT f FROM Friend f WHERE f.account=:account AND friendAccount=:friendAccount";
        try {
            return em.createQuery(q, Friend.class)
                    .setParameter("account", account)
                    .setParameter("friendAccount", friendAccount)
                    .getSingleResult();
        } catch (NoResultException e) {
            logger.log(Level.WARNING, e.getMessage());
            return null;
        }
    }

}
