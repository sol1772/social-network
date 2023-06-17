package com.getjavajob.training.maksyutovs.socialnetwork.service;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.GroupDao;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Group;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.GroupMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupService {

    private static final String TITLE = "title";
    private GroupDao dao;

    public GroupService() {
    }

    @Autowired
    public GroupService(GroupDao dao) {
        this.dao = dao;
    }

    public GroupDao getDao() {
        return dao;
    }

    public void setDao(GroupDao dao) {
        this.dao = dao;
    }

    public Group validateGroup(Group group) {
        Group dbGroup = dao.select(TITLE, group.getTitle());
        if (dbGroup == null) {
            throw new ValidationRuntimeException("Group with title '" + group.getTitle() + "' does not exist");
        }
        return dbGroup;
    }

    public Group getGroupByTitle(String title) {
        return dao.select(TITLE, title);
    }

    public Group getGroupById(int id) {
        return dao.select("id", id);
    }

    public List<Group> getGroupsByString(String substring, int start, int total) {
        return dao.selectByString(substring, start, total);
    }

    public int getGroupsCountByString(String substring, int start, int total) {
        return dao.selectCountByString(substring, start, total);
    }

    public List<Group> getGroupsByAccount(Account account) {
        return dao.selectByAccount(account);
    }

    @Transactional
    public Group createGroup(Group group) {
        Group dbGroup = dao.select(TITLE, group.getTitle());
        if (dbGroup == null) {
            dbGroup = dao.insert(group);
            if (!group.getMembers().isEmpty()) {
                for (GroupMember member : group.getMembers()) {
                    dbGroup.getMembers().add(new GroupMember(dbGroup, member.getAccount(), member.getRole()));
                }
                dbGroup = dao.insert(dbGroup.getMembers());
            }
        }
        return dbGroup;
    }

    @Transactional
    public Group editGroup(Group group, String field, Object value) {
        Group dbGroup = dao.select(TITLE, group.getTitle());
        if (dbGroup != null) {
            dbGroup = dao.update(field, value, group);
        }
        return dbGroup;
    }

    @Transactional
    public Group editGroup(Group group) {
        Group dbGroup = dao.select(TITLE, group.getTitle());
        if (dbGroup != null) {
            dbGroup = dao.update(group);
        }
        return dbGroup;
    }

}