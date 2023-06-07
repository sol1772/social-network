package com.getjavajob.training.maksyutovs.socialnetwork.service;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.GroupDao;
import com.getjavajob.training.maksyutovs.socialnetwork.dao.TransactionManager;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Group;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.GroupMember;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GroupService {

    private static final Logger LOGGER = Logger.getLogger(GroupService.class.getName());
    private static final String TITLE = "title";
    private GroupDao dao;
    private TransactionManager transactionManager;

    public GroupService() {
    }

    public GroupService(GroupDao dao) {
        this.dao = dao;
        this.transactionManager = new TransactionManager(dao.getDataSourceHolder());
    }

    public GroupDao getDao() {
        return dao;
    }

    public void setDao(GroupDao dao) {
        this.dao = dao;
    }

    void rollbackTransaction(Connection con) {
        if (con != null) {
            try {
                con.rollback();
                LOGGER.log(Level.WARNING, "Transaction is being rolled back");
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage());
            }
        }
    }

    public Group getGroupByTitle(String title) {
        return transactionManager.executeAction(() -> dao.select("", TITLE, title));
    }

    public Group getGroupById(int id) {
        return transactionManager.executeAction(() -> dao.select("", "id", id));
    }

    public List<Group> getGroupsByString(String substring, int start, int total) {
        return transactionManager.executeAction(() -> dao.selectByString(substring, start, total));
    }

    public int getGroupsCountByString(String substring, int start, int total) {
        return transactionManager.executeAction(() -> dao.selectCountByString(substring, start, total));
    }

    public List<Group> getGroupsByAccount(Account account) {
        return transactionManager.executeAction(() -> dao.selectByAccount(account));
    }

    public Group createGroup(Group group) {
        return transactionManager.executeTransaction(() -> {
            Group dbGroup = dao.select("", TITLE, group.getTitle());
            if (dbGroup == null) {
                dbGroup = dao.insert("", group);
                if (!group.getMembers().isEmpty()) {
                    for (GroupMember member : group.getMembers()) {
                        dbGroup.getMembers().add(new GroupMember(dbGroup, member.getAccount(), member.getRole()));
                    }
                    dbGroup = dao.insert("", dbGroup.getMembers());
                }
            }
            return dbGroup;
        });
    }

    public Group editGroup(Group group, String field, Object value) {
        return transactionManager.executeTransaction(() -> {
            Group dbGroup = dao.select("", TITLE, group.getTitle());
            if (dbGroup != null) {
                dbGroup = dao.update("", field, value, group);
            }
            return dbGroup;
        });
    }

    public Group editGroup(Group group) {
        return transactionManager.executeTransaction(() -> {
            Group dbGroup = dao.select("", TITLE, group.getTitle());
            if (dbGroup != null) {
                dbGroup = dao.update(group);
            }
            return dbGroup;
        });
    }

}