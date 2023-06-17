package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:dao-context.xml", "classpath:dao-test-context.xml"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GroupDaoTest {

    private static final Logger LOGGER = Logger.getLogger(GroupDaoTest.class.getName());
    private static final String TITLE = "title";
    private static final String EMAIL = "email";
    private static final String DELIMITER = "----------------------------------";
    @Autowired
    private GroupDao dao;
    @Autowired
    private AccountDao accountDAO;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeAll
    void init() {
        initiateTables();
    }

    void initiateTables() {
        // creating tables if not exist
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
                ");";
        jdbcTemplate.execute(query);
        // truncating tables
        query = "TRUNCATE TABLE InterestGroup";
        jdbcTemplate.update(query);
        query = "TRUNCATE TABLE Group_member";
        jdbcTemplate.update(query);
        System.out.println("'InterestGroup' and 'Group_member' tables truncated before adding a new group");
    }

    @Order(1)
    @Test
    void insert() {
        System.out.println(DELIMITER);
        System.out.println("Test GroupDAO.insert(Group)");
        // registering a new group
        Group group = new Group("Figure skating");
        group.setMetaTitle("Figure skating fans group");
        // admin of the group
        String email = "info@alinazagitova.ru";
        Account account = accountDAO.select(EMAIL, email);
        if (account == null) {
            account = new Account("Alina", "Zagitova", "alina_zagitova",
                    LocalDate.parse("2002-05-18", Utils.DATE_FORMATTER), email);
            account.setGender(Gender.F);
            accountDAO.insert(account);
            account = accountDAO.select(EMAIL, email);
        }
        group.setCreatedBy(account.getId());
        Group dbGroup = dao.insert(group);
        assertNotNull(dbGroup);
        System.out.println("Created group " + group);
    }

    @Order(2)
    @Test
    void insertMembers() {
        System.out.println(DELIMITER);
        System.out.println("Test GroupDAO.insert(Members)");
        Group group = dao.select(TITLE, "Figure skating");
        if (group == null) {
            // registering a new group
            group = new Group("Figure skating");
            group.setMetaTitle("Figure skating fans group");
            // admin of the group
            String email = "info@alinazagitova.ru";
            Account account = accountDAO.select(EMAIL, email);
            if (account == null) {
                account = new Account("Alina", "Zagitova", "alina_zagitova",
                        LocalDate.parse("2002-05-18", Utils.DATE_FORMATTER), email);
                account.setGender(Gender.F);
                account.setPasswordHash(account.hashPassword("ComplicatedPassword_2"));
                accountDAO.insert(account);
                account = accountDAO.select(EMAIL, email);
            }
            group.setCreatedBy(account.getId());
            dao.insert(group);
            System.out.println("Created group " + group);
        }

        List<GroupMember> members = group.getMembers();
        Account account1 = accountDAO.select("id", group.getCreatedBy());
        members.add(new GroupMember(group, account1, Role.ADMIN));
        // moderator of the group
        String email2 = "dari@tat.ru";
        Account account2 = accountDAO.select(EMAIL, email2);
        if (account2 == null) {
            account2 = new Account("Darina", "Sabitova", "darisabitova",
                    LocalDate.parse("2007-01-12", Utils.DATE_FORMATTER), email2);
            account2.setGender(Gender.F);
            account2.setPasswordHash(account2.hashPassword("ComplicatedPassword_3"));
            accountDAO.insert(account2);
            account2 = accountDAO.select(EMAIL, email2);
        }
        members.add(new GroupMember(group, account2, Role.MEMBER));
        Group dbGroup = dao.insert(members);
        assertEquals(members.size(), dbGroup.getMembers().size());
        System.out.println("Added members: " + account1 + " and " + account2);
    }

    @Order(3)
    @Test
    void select() {
        System.out.println(DELIMITER);
        System.out.println("Test GroupDAO.select()");
        String title = "Figure skating";
        Group group = dao.select(TITLE, title);
        assertNotNull(group);
        assertEquals(title, group.getTitle());
    }

    @Order(4)
    @Test
    void update() {
        System.out.println(DELIMITER);
        System.out.println("Test GroupDAO.update()");
        String title = "Figure skating";
        Group group = dao.select(TITLE, title);
        assertNotNull(group);
        // updating a field
        String valueToChange = "Figure skating fans group 2023";
        Group dbGroup = dao.update("metaTitle", valueToChange, group);
        assertEquals(valueToChange, dbGroup.getMetaTitle());

        // updating members
        String email = "dari@tat.ru";
        Account account = accountDAO.select(EMAIL, email);
        assertNotNull(account);
        dbGroup = dao.updateGroupMember(new GroupMember(group, account, Role.MODER));
        assertEquals(Role.MODER, Objects.requireNonNull(dbGroup.getMembers().stream().filter(member ->
                Objects.equals(member.getAccount().getEmail(), email)).findAny().orElse(null)).getRole());
        System.out.println("Updated group " + group);
    }

    @Order(5)
    @Test
    void deleteMembers() {
        System.out.println(DELIMITER);
        System.out.println("Test GroupDAO.delete(Members)");
        String title = "Figure skating";
        Group group = dao.select(TITLE, title);
        assertNotNull(group);

        List<GroupMember> members = group.getMembers();
        int initialQuantity = members.size();
        members.clear();
        String email = "dari@tat.ru";
        Account account = accountDAO.select(EMAIL, email);
        assertNotNull(account);
        members.add(new GroupMember(group, account, Role.MODER));
        Group dbGroup = dao.delete(members);
        assertEquals(initialQuantity - 1, dbGroup.getMembers().size());
        System.out.println("Deleted member " + account);
    }

    @Order(6)
    @Test
    void delete() {
        System.out.println(DELIMITER);
        System.out.println("Test GroupDAO.delete()");
        String title = "Figure skating";
        Group group = dao.select(TITLE, title);
        assertNotNull(group);

        Group dbGroup = dao.delete(group);
        assertNull(dbGroup);
        System.out.println("Deleted group " + group);
    }

}