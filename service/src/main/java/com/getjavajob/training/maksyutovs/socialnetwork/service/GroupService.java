package com.getjavajob.training.maksyutovs.socialnetwork.service;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.DaoRuntimeException;
import com.getjavajob.training.maksyutovs.socialnetwork.dao.GroupDao;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GroupService {

    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);
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

    public Group getGroupById(int id) {
        return dao.select(id);
    }

    public Group getFullGroupById(int id) {
        return dao.selectById(id);
    }

    public Group getGroupByTitle(String title) {
        return dao.selectByTitle(title);
    }

    public List<Group> getGroupsByString(String substring, int start, int total) {
        return dao.selectByString(substring, start, total);
    }

    public int getGroupsCountByString(String substring) {
        return dao.selectCountByString(substring);
    }

    public List<Group> getGroupsByAccount(Account account) {
        return dao.selectByAccount(account);
    }

    @Transactional
    public Group createGroup(Group group) {
        Group dbGroup;
        try {
            dbGroup = dao.insert(group);
            if (logger.isInfoEnabled()) {
                logger.info("Created group {}", dbGroup);
            }
        } catch (DaoRuntimeException e) {
            if (logger.isErrorEnabled()) {
                logger.error("Error when creating a group {}", group);
            }
            throw new DaoRuntimeException(e.getMessage(), e);
        }
        return dbGroup;
    }

    @Transactional
    public Group editGroup(Group group) {
        Group dbGroup = dao.update(group);
        if (logger.isInfoEnabled()) {
            logger.info("Updated group {}", dbGroup);
        }
        return dbGroup;

    }

    @Transactional
    public boolean deleteGroup(int id) {
        if (dao.select(id) != null) {
            boolean deleted = dao.delete(id);
            if (deleted) {
                if (logger.isInfoEnabled()) {
                    logger.info("Deleted group with id {}", id);
                }
                return true;
            } else {
                if (logger.isErrorEnabled()) {
                    logger.error("Error when deleting the group with id {}", id);
                }
                return false;
            }
        }
        return false;
    }

}