package com.getjavajob.training.maksyutovs.socialnetwork.service;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.GroupDao;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Group;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.GroupMember;

import java.util.List;
import java.util.Optional;

public class GroupService {

    private static final String TITLE = "title";
    private GroupDao dao;

    public GroupService() {
    }

    public GroupService(GroupDao dao) {
        this.dao = dao;
    }

    public GroupDao getDao() {
        return dao;
    }

    public void setDao(GroupDao dao) {
        this.dao = dao;
    }

    public Group getGroupByTitle(String title) {
        return dao.select("", TITLE, title);
    }

    public Group getGroupById(int id) {
        return dao.select("", "id", id);
    }

    public List<Group> getGroupsByString(String substring, int start, int total) {
        return dao.selectByString(substring, start, total);
    }

    public List<Group> getGroupsByAccount(Account account) {
        return dao.selectByAccount(account);
    }

    public Group createGroup(Group group) {
        Group dbGroup = getGroupByTitle(group.getTitle());
        if (dbGroup == null) {
            dbGroup = dao.insert("", group);
        }
        for (GroupMember member : group.getMembers()) {
            dbGroup.getMembers().add(new GroupMember(dbGroup, member.getAccount(), member.getRole()));
        }
        if (!dbGroup.getMembers().isEmpty()) {
            dbGroup = dao.insert("", dbGroup.getMembers());
        }
        return dbGroup;
    }

    public Optional<Group> editGroup(Group group, String field, Object value) {
        Group dbGroup = getGroupByTitle(group.getTitle());
        if (dbGroup != null) {
            dbGroup = dao.update("", field, value, group);
        }
        return Optional.ofNullable(dbGroup);
    }

    public Optional<Group> editGroup(Group group) {
        Group dbGroup = getGroupByTitle(group.getTitle());
        if (dbGroup != null) {
            dbGroup = dao.update(group);
        }
        return Optional.ofNullable(dbGroup);
    }

}
