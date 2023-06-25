package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:dao-context.xml", "classpath:dao-test-context.xml"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GroupDaoTest {

    private static final Logger LOGGER = Logger.getLogger(GroupDaoTest.class.getName());
    private static final String DELIMITER = "----------------------------------";
    @Autowired
    private GroupDao dao;
    @Autowired
    private AccountDao accountDAO;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeAll
    void init() {
        System.out.println(DELIMITER);
        System.out.println("Test GroupDao.beforeAll");
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
        jdbcTemplate.execute(query);
    }

    void truncateTables() {
        String query = "TRUNCATE TABLE InterestGroup; TRUNCATE TABLE Group_member; TRUNCATE TABLE Account;";
        jdbcTemplate.update(query);
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
        Account admin = accountDAO.select("id", group.getCreatedBy());
        assertNotNull(admin);
        members.add(new GroupMember(group, admin, Role.ADMIN));
        // member of the group
        Account member = accountDAO.insert(getNewTargetAccount());
        assertNotNull(member);
        members.add(new GroupMember(group, member, Role.MEMBER));

        Group dbGroup = dao.insert(members);
        assertEquals(members.size(), dbGroup.getMembers().size());
        System.out.println("Added members: " + admin + " and " + member);
    }

    @Test
    void select() {
        System.out.println(DELIMITER);
        System.out.println("Test GroupDAO.select()");
        assertNotNull(dao.insert(getNewGroup()));
        String title = "Figure skating";
        Group group = dao.select("title", title);
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
        Group dbGroup = dao.update("metaTitle", valueToChange, group);
        assertEquals(valueToChange, dbGroup.getMetaTitle());
        System.out.println("Updated meta-title of the group " + group);

        // updating role MEMBER -> MODER
        Account member = accountDAO.insert(getNewTargetAccount());
        assertNotNull(member);
        group.getMembers().add(new GroupMember(group, member, Role.MEMBER));
        dbGroup = dao.insert(group.getMembers());
        Group updatedGroup = dao.updateGroupMember(new GroupMember(dbGroup, member, Role.MODER));
        assertEquals(Role.MODER, Objects.requireNonNull(updatedGroup.getMembers().stream().filter(finalMember ->
                        Objects.equals(finalMember.getAccount().getEmail(), member.getEmail())).
                findAny().orElse(null)).getRole());
        System.out.println("Updated member " + member + " of the group " + group);
    }

    @Test
    void deleteMembers() {
        System.out.println(DELIMITER);
        System.out.println("Test GroupDAO.delete(Members)");
        Group group = dao.insert(getNewGroup());
        assertNotNull(group);

        Account admin = accountDAO.select("id", group.getCreatedBy());
        assertNotNull(admin);
        group.getMembers().add(new GroupMember(group, admin, Role.ADMIN));
        // member of the group
        Account member = accountDAO.insert(getNewTargetAccount());
        assertNotNull(member);
        group.getMembers().add(new GroupMember(group, member, Role.MEMBER));
        Group dbGroup = dao.insert(group.getMembers());
        int initialQuantity = dbGroup.getMembers().size();

        dbGroup.getMembers().clear();
        dbGroup.getMembers().add(new GroupMember(dbGroup, member, Role.MEMBER));
        Group updatedGroup = dao.delete(dbGroup.getMembers());
        assertEquals(initialQuantity - 1, updatedGroup.getMembers().size());
        System.out.println("Deleted member " + member);
    }

    @Test
    void delete() {
        System.out.println(DELIMITER);
        System.out.println("Test GroupDAO.delete()");
        Group group = dao.insert(getNewGroup());
        assertNotNull(group);

        Group dbGroup = dao.delete(group);
        assertNull(dbGroup);
        System.out.println("Deleted group " + group);
    }

}