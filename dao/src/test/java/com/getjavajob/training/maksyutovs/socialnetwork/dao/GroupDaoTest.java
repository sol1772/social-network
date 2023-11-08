package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import com.getjavajob.training.maksyutovs.socialnetwork.DaoTestConfig;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = DaoTestConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = "/truncate_tables.sql", executionPhase = BEFORE_TEST_METHOD)
@Transactional
class GroupDaoTest {

    private static final String DELIMITER = "----------------------------------";
    @Autowired
    @Qualifier("groupDao")
    private CrudDao<Group> dao;
    @Autowired
    @Qualifier("accountDao")
    private CrudDao<Account> accountDAO;
    @Autowired
    private DataSource dataSource;

    @BeforeAll
    void init() throws SQLException {
        System.out.println(DELIMITER);
        System.out.println("Test GroupDao.beforeAll");
        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("create_tables.sql"));
    }

    Group getNewGroup() {
        // registering a new group
        Group group = new Group("Figure skating");
        group.setMetaTitle("Figure skating fans group");
        // owner of the group
        Account account = accountDAO.insert(getNewAccount());
        group.setCreatedBy(account.getId());
        return group;
    }

    Account getNewAccount() {
        Account account = new Account("Alina", "Zagitova", "alina_zagitova",
                LocalDate.parse("2002-05-18", Utils.DATE_FORMATTER), "info@alinazagitova.ru");
        account.setMiddleName("Ilnazovna");
        account.setGender(Gender.F);
        account.setAddInfo("some info");
        account.setPasswordHash(account.hashPassword("ComplicatedPassword_1"));
        return account;
    }

    Account getNewTargetAccount() {
        Account targetAccount = new Account("Darina", "Sabitova", "darisabitova",
                LocalDate.parse("2007-01-12", Utils.DATE_FORMATTER), "dari@tat.ru");
        targetAccount.setGender(Gender.F);
        targetAccount.setAddInfo("some info");
        targetAccount.setPasswordHash(targetAccount.hashPassword("ComplicatedPassword_2"));
        return targetAccount;
    }

    @Test
    void insert() {
        System.out.println(DELIMITER);
        System.out.println("Test GroupDAO.insert(Group)");
        Group group = dao.insert(getNewGroup());
        assertNotNull(group);
        System.out.println("Created group " + group);
    }

    @Test
    void insertMembers() {
        System.out.println(DELIMITER);
        System.out.println("Test GroupDAO.insert(Members)");
        Group group = dao.insert(getNewGroup());
        assertNotNull(group);

        List<GroupMember> members = group.getMembers();
        // admin of the group
        Account admin = accountDAO.select(group.getCreatedBy());
        assertNotNull(admin);
        members.add(new GroupMember(group, admin, Role.ADMIN));
        // member of the group
        Account member = accountDAO.insert(getNewTargetAccount());
        assertNotNull(member);
        members.add(new GroupMember(group, member, Role.MEMBER));

        Group dbGroup = dao.insert(group);
        assertEquals(members.size(), dbGroup.getMembers().size());
        System.out.println("Added members: " + admin + " and " + member);
    }

    @Test
    void select() {
        System.out.println(DELIMITER);
        System.out.println("Test GroupDAO.select()");
        assertNotNull(dao.insert(getNewGroup()));
        String title = "Figure skating";
        GroupDao groupDao = (GroupDao) DaoTestConfig.unProxyBean(dao);
        Group group = groupDao.selectByTitle(title);
        assertNotNull(group);
        assertEquals(title, group.getTitle());
    }

    @Test
    void update() {
        System.out.println(DELIMITER);
        System.out.println("Test GroupDAO.update()");
        Group group = dao.insert(getNewGroup());
        assertNotNull(group);
        // updating a field 'metaTitle'
        String valueToChange = "Figure skating fans group 2023";
        group.setMetaTitle(valueToChange);
        Group dbGroup = dao.update(group);
        assertEquals(valueToChange, dbGroup.getMetaTitle());
        System.out.println("Updated meta-title of the group " + group);

        // updating group member
        Account member = accountDAO.insert(getNewTargetAccount());
        assertNotNull(member);
        group.getMembers().add(new GroupMember(group, member, Role.MEMBER));
        dbGroup = dao.update(group);
        assertEquals(1, dbGroup.getMembers().size());
        System.out.println("Updated member " + member + " of the group " + group);
    }

    @Test
    void deleteMembers() {
        System.out.println(DELIMITER);
        System.out.println("Test GroupDAO.delete(Members)");
        Group group = dao.insert(getNewGroup());
        assertNotNull(group);

        Account admin = accountDAO.select(group.getCreatedBy());
        assertNotNull(admin);
        group.getMembers().add(new GroupMember(group, admin, Role.ADMIN));
        // member of the group
        Account member = accountDAO.insert(getNewTargetAccount());
        assertNotNull(member);
        group.getMembers().add(new GroupMember(group, member, Role.MEMBER));
        Group dbGroup = dao.insert(group);

        dbGroup.getMembers().clear();
        Group updatedGroup = dao.update(dbGroup);
        assertEquals(0, updatedGroup.getMembers().size());
        System.out.println("Deleted members of group " + updatedGroup);
    }

    @Test
    void delete() {
        System.out.println(DELIMITER);
        System.out.println("Test GroupDAO.delete()");
        Group group = dao.insert(getNewGroup());
        assertNotNull(group);
        assertTrue(dao.delete(group.getId()));
        System.out.println("Deleted group " + group);
    }

}