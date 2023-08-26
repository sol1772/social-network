package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Friend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;

@Repository
public class FriendDao extends AbstractCrudDao<Friend> {

    private static final Logger logger = LoggerFactory.getLogger(FriendDao.class);

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
            logger.error(e.getMessage());
            return null;
        }
    }

}
