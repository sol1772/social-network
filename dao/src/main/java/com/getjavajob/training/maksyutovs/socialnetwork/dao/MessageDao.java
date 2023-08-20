package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Message;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.MessageType;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class MessageDao extends AbstractCrudDao<Message> {

    private static final Logger logger = Logger.getLogger(MessageDao.class.getName());

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
            logger.log(Level.WARNING, e.getMessage());
            return messages;
        }
    }

}
