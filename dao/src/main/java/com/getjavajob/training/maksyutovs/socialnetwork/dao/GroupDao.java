package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Group;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.GroupMember;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Role;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GroupDao implements CrudDao<Group, Object> {

    private static final Logger LOGGER = Logger.getLogger(GroupDao.class.getName());
    private static final String CREATE = "INSERT INTO ";
    private static final String READ = "SELECT * FROM ";
    private static final String UPDATE = "UPDATE ";
    private static final String DELETE = "DELETE FROM ";
    private static final String TITLE = "title";
    private ConnectionPool pool;
    private Connection connection;

    public GroupDao() {
    }

    public GroupDao(Properties properties) {
        this.pool = ConnectionPool.getInstance(properties);
    }

    public GroupDao(ConnectionPool pool) {
        this.pool = pool;
    }

    public GroupDao(Connection connection) {
        this.connection = connection;
        this.pool = ConnectionPool.getInstance(new Properties());
    }

    public ConnectionPool getPool() {
        return pool;
    }

    public Connection getConnection() {
        return connection;
    }

    void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
            }
        }
    }

    private Group createGroupFromResult(ResultSet rs) throws SQLException {
        Group group = new Group(rs.getString(TITLE));
        group.setId(rs.getInt("id"));
        group.setCreatedBy(rs.getInt("createdBy"));
        group.setMetaTitle(rs.getString("metaTitle"));
        String createdAt = rs.getString("createdAt");
        group.setCreatedAt(createdAt == null ? LocalDateTime.of(0, 1, 1, 0, 0) :
                LocalDateTime.parse(createdAt.substring(0, Utils.DATE_TIME_PATTERN.length()), Utils.DATE_TIME_FORMATTER));
        group.setImage(rs.getBytes("image"));
        return group;
    }

    @Override
    public Group insert(String query, Group group) {
        connection = pool.getConnection();
        if (query.isEmpty()) {
            query = "InterestGroup(title,metaTitle,createdBy,createdAt,image) VALUES (?,?,?,now(),?);";
        }
        String queryInsert = CREATE + query;
        try (PreparedStatement pst = connection.prepareStatement(queryInsert)) {
            if (queryInsert.contains("?")) {
                pst.setString(1, group.getTitle());
                pst.setString(2, group.getMetaTitle());
                pst.setInt(3, group.getCreatedBy());
                pst.setBytes(4, group.getImage());
            }
            pst.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            connection = null;
        }
        return select("", TITLE, group.getTitle());
    }

    public Group insert(String query, List<GroupMember> members) {
        connection = pool.getConnection();
        Group group;
        if (members.isEmpty()) {
            return null;
        } else {
            group = members.get(0).getGroup();
        }
        if (query.isEmpty()) {
            query = "Group_member(groupId,accId,roleType) VALUES (?,?,?);";
        }
        String queryInsert = CREATE + query;
        try (PreparedStatement pst = connection.prepareStatement(queryInsert)) {
            for (GroupMember member : members) {
                if (queryInsert.contains("?")) {
                    pst.setInt(1, member.getGroupId());
                    pst.setInt(2, member.getAccount().getId());
                    pst.setString(3, member.getRole().toString());
                }
                pst.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            connection = null;
        }
        return select("", TITLE, group.getTitle());
    }

    @Override
    public Group select(String query, String field, Object value) {
        connection = pool.getConnection();
        Group group = null;
        ResultSet rs = null;
        if (query.isEmpty()) {
            query = "InterestGroup WHERE " + field + "=?;";
        }
        String querySelect = READ + query;
        try (PreparedStatement pst = connection.prepareStatement(querySelect)) {
            if (querySelect.contains("?")) {
                if (value instanceof String) {
                    pst.setString(1, (String) value);
                } else if (value instanceof Integer) {
                    pst.setInt(1, (int) value);
                }
            }
            rs = pst.executeQuery();
            if (rs.next()) {
                group = createGroupFromResult(rs);

                // members
                String queryMembers = "SELECT * FROM Group_member gm INNER JOIN Account a " +
                        "ON gm.accId = a.id WHERE groupId=?;";
                try (PreparedStatement pstMembers = connection.prepareStatement(queryMembers)) {
                    pstMembers.setInt(1, group.getId());
                    rs = pstMembers.executeQuery();
                    List<GroupMember> members = group.getMembers();
                    while (rs.next()) {
                        Account account = new AccountDao().createAccountFromResult(rs);
                        members.add(new GroupMember(group, account, Role.valueOf(rs.getString("roleType"))));
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            closeResultSet(rs);
            connection = null;
        }
        return group;
    }

    public List<Group> selectByString(String substring, int start, int total) {
        connection = pool.getConnection();
        List<Group> groups = new ArrayList<>();
        String searchString = "%" + substring + "%";
        String querySelect = READ + "InterestGroup WHERE title LIKE ? ORDER BY title " +
                (total > 0 ? " LIMIT " + (start - 1) + "," + total : "");
        ResultSet rs = null;
        try (PreparedStatement pst = connection.prepareStatement(querySelect)) {
            pst.setString(1, searchString);
            rs = pst.executeQuery();
            while (rs.next()) {
                Group group = createGroupFromResult(rs);
                groups.add(group);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            closeResultSet(rs);
            connection = null;
        }
        return groups;
    }

    public int selectCountByString(String substring, int start, int total) {
        connection = pool.getConnection();
        int rows = 0;
        String searchString = "%" + substring + "%";
        String querySelect = READ + "InterestGroup WHERE title LIKE ? ORDER BY title " +
                (total > 0 ? " LIMIT " + (start - 1) + "," + total : "");
        querySelect = querySelect.replace("SELECT *", "SELECT COUNT(*) AS count");
        ResultSet rs = null;
        try (PreparedStatement pst = connection.prepareStatement(querySelect)) {
            pst.setString(1, searchString);
            rs = pst.executeQuery();
            if (rs.next()) {
                rows = rs.getInt("count");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            closeResultSet(rs);
            connection = null;
        }
        return rows;
    }

    public List<Group> selectByAccount(Account account) {
        connection = pool.getConnection();
        List<Group> groups = new ArrayList<>();
        String querySelect = READ + "InterestGroup ig INNER JOIN Group_member gm " +
                "ON ig.id = gm.groupId WHERE accId = ?";
        ResultSet rs = null;
        try (PreparedStatement pst = connection.prepareStatement(querySelect)) {
            pst.setInt(1, account.getId());
            rs = pst.executeQuery();
            while (rs.next()) {
                Group group = createGroupFromResult(rs);
                groups.add(group);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            closeResultSet(rs);
            connection = null;
        }
        return groups;
    }

    @Override
    public Group update(String query, String field, Object value, Group group) {
        connection = pool.getConnection();
        if (query.isEmpty()) {
            query = "InterestGroup SET " + field + "=? WHERE title=?;";
        }
        String queryUpdate = UPDATE + query;
        try (PreparedStatement pst = connection.prepareStatement(queryUpdate)) {
            if (queryUpdate.contains("?")) {
                if (value instanceof String) {
                    pst.setString(1, (String) value);
                } else if (value instanceof Integer) {
                    pst.setInt(1, (int) value);
                } else if (value instanceof InputStream) {
                    pst.setBinaryStream(1, (InputStream) value);
                }
                pst.setString(2, group.getTitle());
            }
            pst.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            connection = null;
        }
        return select("", TITLE, group.getTitle());
    }

    public Group update(Group group) {
        connection = pool.getConnection();
        String query = "InterestGroup SET metaTitle =?, image=? WHERE title=?;";
        String queryUpdate = UPDATE + query;
        try (PreparedStatement pst = connection.prepareStatement(queryUpdate)) {
            pst.setString(1, group.getMetaTitle());
            pst.setBinaryStream(2, new ByteArrayInputStream(group.getImage()));
            pst.setString(3, group.getTitle());
            pst.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            connection = null;
        }
        return select("", TITLE, group.getTitle());
    }

    @Override
    public Group delete(String query, Group group) {
        connection = pool.getConnection();
        if (query.isEmpty()) {
            query = "InterestGroup WHERE title=?;" +
                    "DELETE FROM Group_member WHERE groupId=?;";
        }
        String queryDelete = DELETE + query;
        try (PreparedStatement pst = connection.prepareStatement(queryDelete)) {
            if (queryDelete.contains("?")) {
                int groupId = group.getId();
                pst.setString(1, group.getTitle());
                pst.setInt(2, groupId);
            }
            pst.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            connection = null;
        }
        return select("", TITLE, group.getTitle());
    }

    public Group delete(String query, List<GroupMember> members) {
        connection = pool.getConnection();
        Group group;
        if (members.isEmpty()) {
            return null;
        } else {
            group = members.get(0).getGroup();
        }
        if (query.isEmpty()) {
            query = "Group_member WHERE groupId=? AND accId=? AND roleType=?;";
        }
        String queryDelete = DELETE + query;
        try (PreparedStatement pst = connection.prepareStatement(queryDelete)) {
            for (GroupMember member : members) {
                if (queryDelete.contains("?")) {
                    pst.setInt(1, member.getGroupId());
                    pst.setInt(2, member.getAccount().getId());
                    pst.setString(3, member.getRole().toString());
                }
                pst.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            connection = null;
        }
        return select("", TITLE, group.getTitle());
    }

}