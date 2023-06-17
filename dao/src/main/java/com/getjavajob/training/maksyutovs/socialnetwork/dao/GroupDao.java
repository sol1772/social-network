package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Group;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.GroupMember;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class GroupDao implements CrudDao<Group, Object> {

    private static final String CREATE = "INSERT INTO ";
    private static final String READ = "SELECT * FROM ";
    private static final String UPDATE = "UPDATE ";
    private static final String DELETE = "DELETE FROM ";
    private static final String TITLE = "title";
    private static final String ID = "id";
    private JdbcTemplate jdbcTemplate;

    public GroupDao() {
    }

    @Autowired
    public GroupDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Group createGroupFromResult(ResultSet rs) throws SQLException {
        Group group;
        group = new Group(rs.getString(TITLE));
        group.setId(rs.getInt("id"));
        group.setCreatedBy(rs.getInt("createdBy"));
        group.setMetaTitle(rs.getString("metaTitle"));
        group.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
        group.setImage(rs.getBytes("image"));
        return group;
    }

    private GroupMember createGroupMemberFromResult(ResultSet rs, Group group) throws SQLException {
        Account account = new AccountDao().createAccountFromResult(rs);
        return new GroupMember(group, account, Role.valueOf(rs.getString("roleType")));
    }

    @Override
    public Group insert(Group group) {
        String queryInsert = CREATE + "InterestGroup(title,metaTitle,createdBy,createdAt,image) VALUES (?,?,?,now(),?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement pst = con.prepareStatement(queryInsert, new String[]{"id"});
            pst.setString(1, group.getTitle());
            pst.setString(2, group.getMetaTitle());
            pst.setInt(3, group.getCreatedBy());
            pst.setBytes(4, group.getImage());
            return pst;
        }, keyHolder);
        return select(ID, keyHolder.getKey());
    }

    public Group insert(List<GroupMember> members) {
        if (members.isEmpty()) {
            throw new IllegalArgumentException("No data to insert");
        }
        Group group = members.get(0).getGroup();
        String queryInsert = CREATE + "Group_member(groupId,accId,roleType) VALUES (?,?,?);";
        for (GroupMember member : members) {
            jdbcTemplate.update(queryInsert, member.getGroupId(), member.getAccount().getId(),
                    member.getRole().toString());
        }
        return select(TITLE, group.getTitle());
    }

    @Override
    public Group select(String field, Object value) {
        String querySelect = READ + "InterestGroup WHERE " + field + "=?;";
        Group group = jdbcTemplate.query(querySelect, (rs, rowNum) -> createGroupFromResult(rs), value)
                .stream().findAny().orElse(null);

        // members
        if (group != null) {
            String queryMembers = "SELECT * FROM Group_member gm INNER JOIN Account a " +
                    "ON gm.accId = a.id WHERE groupId=?;";
            group.getMembers().addAll(jdbcTemplate.query(queryMembers, (rs, rowNum) ->
                    createGroupMemberFromResult(rs, group), group.getId()));
        }
        return group;
    }

    public List<Group> selectByString(String substring, int start, int total) {
        String searchString = "%" + substring + "%";
        String querySelect = READ + "InterestGroup WHERE title LIKE ? ORDER BY title " +
                (total > 0 ? " LIMIT " + (start - 1) + "," + total : "");
        return jdbcTemplate.query(querySelect, (rs, rowNum) -> createGroupFromResult(rs), searchString);
    }

    public Integer selectCountByString(String substring, int start, int total) {
        String searchString = "%" + substring + "%";
        String querySelect = READ + "InterestGroup WHERE title LIKE ? ORDER BY title " +
                (total > 0 ? " LIMIT " + (start - 1) + "," + total : "");
        querySelect = querySelect.replace("SELECT *", "SELECT COUNT(*) AS count");
        return jdbcTemplate.queryForObject(querySelect, Integer.class, searchString);
    }

    public List<Group> selectByAccount(Account account) {
        String querySelect = READ + "InterestGroup ig INNER JOIN Group_member gm " +
                "ON ig.id = gm.groupId WHERE accId = ?";
        return jdbcTemplate.query(querySelect, (rs, rowNum) -> createGroupFromResult(rs), account.getId());
    }

    @Override
    public Group update(String field, Object value, Group group) {
        String queryUpdate = UPDATE + "InterestGroup SET " + field + "=? WHERE title=?;";
        jdbcTemplate.update(queryUpdate, value, group.getTitle());
        return select(TITLE, group.getTitle());
    }

    public Group update(Group group) {
        String queryUpdate = UPDATE + "InterestGroup SET metaTitle =?, image=? WHERE title=?;";
        jdbcTemplate.update(queryUpdate, group.getMetaTitle(),
                new ByteArrayInputStream(group.getImage()), group.getTitle());
        return select(TITLE, group.getTitle());
    }

    public Group updateGroupMember(GroupMember groupMember) {
        String queryUpdate = UPDATE + "Group_member SET roleType=? WHERE groupId=? AND accId=?;";
        jdbcTemplate.update(queryUpdate, groupMember.getRole().toString(),
                groupMember.getGroupId(), groupMember.getAccount().getId());
        return select(TITLE, groupMember.getGroup().getTitle());
    }

    @Override
    public Group delete(Group group) {
        String queryDelete = DELETE + "InterestGroup WHERE title=?;" +
                DELETE + "Group_member WHERE groupId=?;";
        jdbcTemplate.update(queryDelete, group.getTitle(), group.getId());
        return select(TITLE, group.getTitle());
    }

    public Group delete(List<GroupMember> members) {
        if (members.isEmpty()) {
            throw new IllegalArgumentException("No data to delete");
        }
        Group group = members.get(0).getGroup();
        String queryDelete = DELETE + "Group_member WHERE groupId=? AND accId=? AND roleType=?;";
        for (GroupMember member : members) {
            jdbcTemplate.update(queryDelete,
                    member.getGroupId(), member.getAccount().getId(), member.getRole().toString());
        }
        return select(TITLE, group.getTitle());
    }

}