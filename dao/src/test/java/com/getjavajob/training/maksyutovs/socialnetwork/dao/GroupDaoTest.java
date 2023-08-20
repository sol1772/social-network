package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:dao-context.xml", "classpath:dao-test-context.xml"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class GroupDaoTest {

    private static final Logger LOGGER = Logger.getLogger(GroupDaoTest.class.getName());
    private static final String DELIMITER = "----------------------------------";
    @Autowired
    private GroupDao dao;
    @Autowired
    private AccountDao accountDAO;
    private EntityManager em;

    @BeforeAll
    void init() {
        System.out.println(DELIMITER);
        System.out.println("Test GroupDao.beforeAll");
        em = Persistence.createEntityManagerFactory("test").createEntityManager();
        createTablesIfNotExist();
    }

    @BeforeEach
    void truncate() {
        truncateTables();
    }

    void createTablesIfNotExist() {
        String query = "CREATE TABLE if not exists InterestGroup (" +
                "    id INT AUTO_INCREMENT," +
                "    createdBy INT, " +
                "    title VARCHAR(50) NOT NULL UNIQUE, " +
                "    metaTitle VARCHAR(100), " +
                "    createdAt DATETIME," +
                "    PRIMARY KEY(id)" +
                ");" +
                "CREATE TABLE if not exists Group_member (" +
                "    id INT AUTO_INCREMENT," +
                "    accId INT NOT NULL," +
                "    groupId INT NOT NULL, " +
                "    roleType ENUM('ADMIN', 'MODER', 'MEMBER')," +
                "    PRIMARY KEY(id)," +
                "    CONSTRAINT Uq_members UNIQUE (accId, groupId)" +
                ");" +
                "CREATE TABLE if not exists Account (" +
                "    id INT AUTO_INCREMENT," +
                "    firstName VARCHAR(30) NOT NULL," +
                "    lastName VARCHAR(30) NOT NULL," +
                "    middleName VARCHAR(30)," +
                "    username VARCHAR(15) NOT NULL," +
                "    email VARCHAR(50) NOT NULL UNIQUE," +
                "    dateOfBirth DATE NOT NULL," +
                "    gender ENUM('M', 'F')," +
                "    addInfo VARCHAR(100)," +
                "    passwordHash VARCHAR(128)," +
                "    registeredAt DATETIME," +
                "    image BLOB," +
                "    PRIMARY KEY(id)" +
                ");";
        try {
            em.getTransaction().begin();
            em.createNativeQuery(query).executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
    }

    void truncateTables() {
        String query = "TRUNCATE TABLE InterestGroup; TRUNCATE TABLE Group_member; TRUNCATE TABLE Account;";
        try {
            em.getTransaction().begin();
            em.createNativeQuery(query).executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
        LOGGER.log(Level.CONFIG, "'InterestGroup', 'Group_member' and 'Account' tables truncated");
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
        Group group = dao.selectByTitle(title);
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