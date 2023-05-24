package com.getjavajob.training.maksyutovs.socialnetwork.service;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.ConnectionPool;
import com.getjavajob.training.maksyutovs.socialnetwork.dao.GroupDao;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Group;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.GroupMember;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GroupService {

    private static final Logger LOGGER = Logger.getLogger(GroupService.class.getName());
    private static final String TITLE = "title";
    private GroupDao dao;
    private ConnectionPool pool;

    private Connection connection;

    public GroupService() {
    }

    public GroupService(GroupDao dao) {
        this.dao = dao;
        this.pool = dao.getPool();
    }

    public GroupDao getDao() {
        return dao;
    }

    public void setDao(GroupDao dao) {
        this.dao = dao;
    }

    public ConnectionPool getPool() {
        return pool;
    }

    public void setPool(ConnectionPool pool) {
        this.pool = pool;
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
        connection = pool.getConnection();
        Group dbGroup = dao.select("", TITLE, title);
        pool.returnConnection(connection);
        return dbGroup;
    }

    public Group getGroupById(int id) {
        connection = pool.getConnection();
        Group dbGroup = dao.select("", "id", id);
        pool.returnConnection(connection);
        return dbGroup;
    }

    public List<Group> getGroupsByString(String substring, int start, int total) {
        connection = pool.getConnection();
        List<Group> groups = dao.selectByString(substring, start, total);
        pool.returnConnection(connection);
        return groups;
    }

    public int getGroupsCountByString(String substring, int start, int total) {
        connection = pool.getConnection();
        int rows = dao.selectCountByString(substring, start, total);
        pool.returnConnection(connection);
        return rows;
    }

    public List<Group> getGroupsByAccount(Account account) {
        connection = pool.getConnection();
        List<Group> groups = dao.selectByAccount(account);
        pool.returnConnection(connection);
        return groups;
    }

    public Group createGroup(Group group) {
        Group dbGroup = null;
        try {
            connection = pool.getConnection();
            connection.setAutoCommit(false);
            dbGroup = dao.select("", TITLE, group.getTitle());
            if (dbGroup == null) {
                dbGroup = dao.insert("", group);
            }
            for (GroupMember member : group.getMembers()) {
                dbGroup.getMembers().add(new GroupMember(dbGroup, member.getAccount(), member.getRole()));
            }
            if (!dbGroup.getMembers().isEmpty()) {
                dbGroup = dao.insert("", dbGroup.getMembers());
            }
            connection.commit();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
            rollbackTransaction(connection);
        } finally {
            pool.returnConnection(connection);
        }
        return dbGroup;
    }

    public Optional<Group> editGroup(Group group, String field, Object value) {
        Group dbGroup = null;
        try {
            connection = pool.getConnection();
            connection.setAutoCommit(false);
            dbGroup = dao.select("", TITLE, group.getTitle());
            if (dbGroup != null) {
                dbGroup = dao.update("", field, value, group);
            }
            connection.commit();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
            rollbackTransaction(connection);
        } finally {
            pool.returnConnection(connection);
        }
        return Optional.ofNullable(dbGroup);
    }

    public Optional<Group> editGroup(Group group) {
        Group dbGroup = null;
        try {
            connection = pool.getConnection();
            connection.setAutoCommit(false);
            dbGroup = dao.select("", TITLE, group.getTitle());
            if (dbGroup != null) {
                dbGroup = dao.update(group);
            }
            connection.commit();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
            rollbackTransaction(connection);
        } finally {
            pool.returnConnection(connection);
        }
        return Optional.ofNullable(dbGroup);
    }

}
