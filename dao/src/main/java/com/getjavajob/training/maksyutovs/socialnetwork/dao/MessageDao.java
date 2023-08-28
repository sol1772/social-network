package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Message;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MessageDao extends AbstractCrudDao<Message> {

    private static final Logger logger = LoggerFactory.getLogger(MessageDao.class);

    public MessageDao() {
        super(Message.class);
    }

    public List<Message> selectMessages(Account account, Account targetAccount, MessageType type) {
        List<Message> messages = new ArrayList<>();
        String q = "SELECT m FROM Message m WHERE (m.account=:account AND m.targetAccount=:trgAccount)" +
                "OR (m.account=:trgAccount AND m.targetAccount=:account) AND m.msgType=:type ORDER BY m.createdAt DESC";
        try {
            messages = em.createQuery(q, Message.class)
                    .setParameter("account", account)
                    .setParameter("trgAccount", targetAccount)
                    .setParameter("type", type)
                    .getResultList();
            return messages;
        } catch (NoResultException e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage());
            }
            return messages;
        }
    }

}
