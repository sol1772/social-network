package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Group;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class GroupDao implements CrudDao<Group, Object> {

    private static final String CREATE = "INSERT INTO ";
    private static final String READ = "SELECT * FROM ";
    private static final String UPDATE = "UPDATE ";
    private static final String DELETE = "DELETE FROM ";
    public final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private ConnectionPool pool;
    private Connection connection;

    public GroupDao() {
    }

    public GroupDao(Connection connection) {
        this.connection = connection;
    }

    public GroupDao(String resourceName) {
        pool = new ConnectionPool(resourceName);
        connection = pool.getConnection();
    }

    public Connection getConnection() {
        return connection;
    }

    private Group clone(Group group) {
        Group dbgroup = new Group(group.getTitle());
        group.setId(group.getId());
        group.setCreatedBy(group.getCreatedBy());
        group.setMetaTitle(group.getMetaTitle());
        group.setCreatedAt(group.getCreatedAt());
        for (Group.GroupMember member : group.getMembers()) {
            dbgroup.getMembers().add(dbgroup.new GroupMember(member.getAccount(), member.getRole()));
        }
        return dbgroup;
    }

    private Group createGroupFromResult(ResultSet rs) throws SQLException, ParseException {
        Group group = new Group(rs.getString("title"));
        group.setId(rs.getInt("id"));
        group.setCreatedBy(rs.getInt("createdBy"));
        group.setMetaTitle(rs.getString("metaTitle"));
        group.setCreatedAt(rs.getString("createdAt") == null ? new Date(0) :
                formatter.parse(rs.getString("createdAt")));
        return group;
    }

    @Override
    public Group insert(String query, Group group) {
        StringBuilder sb = new StringBuilder(CREATE);
        if (query.isEmpty()) {
            query = "InterestGroup(title,metaTitle,createdBy,createdAt) VALUES (?,?,?,now());";
        }
        String queryInsert = sb.append(query).toString();
        try (PreparedStatement pst = connection.prepareStatement(queryInsert)) {
            connection.setAutoCommit(false);
            if (queryInsert.contains("?")) {
                pst.setString(1, group.getTitle());
                pst.setString(2, group.getMetaTitle());
                pst.setInt(3, group.getCreatedBy());
            }
            pst.executeUpdate();
            connection.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return select("", "Title", group.getTitle());
    }

    public Group insert(String query, List<Group.GroupMember> members) {
        Group group;
        if (members.isEmpty()) {
            return null;
        } else {
            group = members.get(0).getGroup();
        }

        StringBuilder sb = new StringBuilder(CREATE);
        if (query.isEmpty()) {
            query = "Group_member(groupId,accId,roleType) VALUES (?,?,?);";
        }
        String queryInsert = sb.append(query).toString();
        try (PreparedStatement pst = connection.prepareStatement(queryInsert)) {
            connection.setAutoCommit(false);
            for (Group.GroupMember member : members) {
                if (queryInsert.contains("?")) {
                    pst.setInt(1, member.getGroupId());
                    pst.setInt(2, member.getAccount().getId());
                    pst.setString(3, member.getRole().toString());
                }
                pst.executeUpdate();
                connection.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return select("", "Title", group.getTitle());
    }

    @Override
    public Group select(String query, String field, Object value) {
        Group group = null;
        ResultSet rs = null;
        StringBuilder sb = new StringBuilder(READ);
        if (query.isEmpty()) {
            query = "InterestGroup WHERE " + field + "=?;";
        }
        String querySelect = sb.append(query).toString();
        try (PreparedStatement pst = connection.prepareStatement(querySelect)) {
            if (querySelect.contains("?")) {
                try {
                    pst.setString(1, (String) value);
                } catch (ClassCastException e) {
                    try {
                        pst.setInt(1, (int) value);
                    } catch (ClassCastException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            rs = pst.executeQuery();
            if (rs.next()) {
                group = createGroupFromResult(rs);

                // members
                String queryMembers = "SELECT * FROM Group_member WHERE groupId=?;";
                try (PreparedStatement pstMembers = connection.prepareStatement(queryMembers)) {
                    pstMembers.setInt(1, group.getId());
                    rs = pstMembers.executeQuery();
                    List<Group.GroupMember> members = group.getMembers();
                    while (rs.next()) {
                        Account account = new AccountDao(connection).select("", "id", rs.getInt("accId"));
                        members.add(group.new GroupMember(account, Group.Role.valueOf(rs.getString("roleType"))));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return group;
    }

    @Override
    public Group update(String query, String field, Object value, Group group) {
        StringBuilder sb = new StringBuilder(UPDATE);
        if (query.isEmpty()) {
            query = "InterestGroup SET " + field + "=? WHERE TITLE=?;";
        }
        String queryUpdate = sb.append(query).toString();
        try (PreparedStatement pst = connection.prepareStatement(queryUpdate)) {
            connection.setAutoCommit(false);
            if (queryUpdate.contains("?")) {
                try {
                    pst.setString(1, (String) value);
                    pst.setString(2, group.getTitle());
                } catch (ClassCastException e) {
                    try {
                        pst.setInt(1, (int) value);
                        pst.setString(2, group.getTitle());
                    } catch (ClassCastException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            pst.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return select("", "Title", group.getTitle());
    }

    @Override
    public Group delete(String query, Group group) {
        if (query.isEmpty()) {
            query = "InterestGroup WHERE TITLE=?;" +
                    "DELETE FROM Group_member WHERE groupId=?;";
        }
        String queryDelete = DELETE + query;
        try (PreparedStatement pst = connection.prepareStatement(queryDelete)) {
            connection.setAutoCommit(false);
            if (queryDelete.contains("?")) {
                int groupId = group.getId();
                pst.setString(1, group.getTitle());
                pst.setInt(2, groupId);
            }
            pst.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return select("", "Title", group.getTitle());
    }

    public Group delete(String query, List<Group.GroupMember> members) {
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
            connection.setAutoCommit(false);
            for (Group.GroupMember member : members) {
                if (queryDelete.contains("?")) {
                    pst.setInt(1, member.getGroupId());
                    pst.setInt(2, member.getAccount().getId());
                    pst.setString(3, member.getRole().toString());
                }
                pst.executeUpdate();
                connection.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return select("", "Title", group.getTitle());
    }

}