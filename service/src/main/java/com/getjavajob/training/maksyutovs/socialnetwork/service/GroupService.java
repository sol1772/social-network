package com.getjavajob.training.maksyutovs.socialnetwork.service;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.DaoException;
import com.getjavajob.training.maksyutovs.socialnetwork.dao.DaoRuntimeException;
import com.getjavajob.training.maksyutovs.socialnetwork.dao.DataSourceHolder;
import com.getjavajob.training.maksyutovs.socialnetwork.dao.GroupDao;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Group;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.GroupMember;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GroupService {

    private static final Logger LOGGER = Logger.getLogger(GroupService.class.getName());
    private static final String TITLE = "title";
    private GroupDao dao;
    private DataSourceHolder dataSourceHolder;

    public GroupService() {
    }

    public GroupService(GroupDao dao) {
        this.dao = dao;
        this.dataSourceHolder = dao.getDataSourceHolder();
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
        Group dbGroup = null;
        try (Connection ignored = dataSourceHolder.getConnection()) {
            dbGroup = dao.select("", TITLE, title);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
        return dbGroup;
    }

    public Group getGroupById(int id) {
        Group dbGroup = null;
        try (Connection ignored = dataSourceHolder.getConnection()) {
            dbGroup = dao.select("", "id", id);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
        return dbGroup;
    }

    public List<Group> getGroupsByString(String substring, int start, int total) {
        List<Group> groups = Collections.emptyList();
        try (Connection ignored = dataSourceHolder.getConnection()) {
            groups = dao.selectByString(substring, start, total);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
        return groups;
    }

    public int getGroupsCountByString(String substring, int start, int total) {
        int rows = 0;
        try (Connection ignored = dataSourceHolder.getConnection()) {
            rows = dao.selectCountByString(substring, start, total);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
        return rows;
    }

    public List<Group> getGroupsByAccount(Account account) {
        List<Group> groups = Collections.emptyList();
        try (Connection ignored = dataSourceHolder.getConnection()) {
            groups = dao.selectByAccount(account);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
        return groups;
    }

    public Group createGroup(Group group) {
        Group dbGroup = null;
        try (Connection connection = dataSourceHolder.getConnection()) {
            boolean initialAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
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
            } catch (DaoException | DaoRuntimeException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
                rollbackTransaction(connection);
            } finally {
                connection.setAutoCommit(initialAutoCommit);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
        return dbGroup;
    }

    public Optional<Group> editGroup(Group group, String field, Object value) {
        Group dbGroup = null;
        try (Connection connection = dataSourceHolder.getConnection()) {
            boolean initialAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                dbGroup = dao.select("", TITLE, group.getTitle());
                if (dbGroup != null) {
                    dbGroup = dao.update("", field, value, group);
                }
                connection.commit();
            } catch (DaoException | DaoRuntimeException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
                rollbackTransaction(connection);
            } finally {
                connection.setAutoCommit(initialAutoCommit);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
        return Optional.ofNullable(dbGroup);
    }

    public Optional<Group> editGroup(Group group) {
        Group dbGroup = null;
        try (Connection connection = dataSourceHolder.getConnection()) {
            boolean initialAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                dbGroup = dao.select("", TITLE, group.getTitle());
                if (dbGroup != null) {
                    dbGroup = dao.update(group);
                }
                connection.commit();
            } catch (DaoException | DaoRuntimeException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
                rollbackTransaction(connection);
            } finally {
                connection.setAutoCommit(initialAutoCommit);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
        return Optional.ofNullable(dbGroup);
    }

}
