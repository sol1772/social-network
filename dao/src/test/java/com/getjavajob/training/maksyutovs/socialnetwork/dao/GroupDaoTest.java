package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.*;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GroupDaoTest {

    private static final String resourceName = "/h2.properties";
    private static final String TITLE = "title";
    private static final String EMAIL = "email";
    private static GroupDao dao;
    private static Connection con;
    private static Statement st;

    @BeforeAll
    static void connect() {
        dao = new GroupDao(resourceName);
        con = con == null ? dao.getPool().getConnection() : con;
        try {
            con.setAutoCommit(false);
            st = con.createStatement();
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
            st.executeUpdate(query);
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @AfterAll
    static void closeConnection() {
        try {
            if (st != null) {
                st.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dao.getPool().closeConnections();
    }

    void truncateTables() {
        String query = "TRUNCATE TABLE InterestGroup";
        try {
            con.setAutoCommit(false);
            st.executeUpdate(query);
            query = "TRUNCATE TABLE Group_member";
            st.executeUpdate(query);
            System.out.println("'InterestGroup' and 'Group_member' tables truncated before adding a new group");
        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Order(1)
    @Test
    void insert() {
        System.out.println("---------------------------------");
        System.out.println("Test GroupDAO.insert(Group)");
        Group group;
        AccountDao accountDAO = new AccountDao(con);
        try {
            truncateTables();
            // registering new group
            group = new Group("Figure skating");
            group.setMetaTitle("Figure skating fans group");
            // admin of the group
            String email = "info@alinazagitova.ru";
            Account account = accountDAO.select("", EMAIL, email);
            if (account == null) {
                account = new Account("Alina", "Zagitova", "alina_zagitova",
                        dao.formatter.parse("2002-05-18"), email);
                account.setGender(Gender.F);
                accountDAO.insert("", account);
                account = accountDAO.select("", EMAIL, email);
            }
            group.setCreatedBy(account.getId());
            Group dbGroup = dao.insert("", group);
            assertNotNull(dbGroup);
            System.out.println("Created group " + group);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
    }

    @Order(2)
    @Test
    void insertMembers() {
        System.out.println("---------------------------------");
        System.out.println("Test GroupDAO.insert(Members)");
        Group group;
        AccountDao accountDAO = new AccountDao(con);
        try {
            group = dao.select("", TITLE, "Figure skating");
            if (group == null) {
                // registering new group
                group = new Group("Figure skating");
                group.setMetaTitle("Figure skating fans group");
                // admin of the group
                String email = "info@alinazagitova.ru";
                Account account = accountDAO.select("", EMAIL, email);
                if (account == null) {
                    account = new Account("Alina", "Zagitova", "alina_zagitova",
                            dao.formatter.parse("2002-05-18"), email);
                    account.setGender(Gender.F);
                    account.setPasswordHash(account.hashPassword("ComplicatedPassword_2"));
                    accountDAO.insert("", account);
                    account = accountDAO.select("", EMAIL, email);
                }
                group.setCreatedBy(account.getId());
                dao.insert("", group);
                System.out.println("Created group " + group);
            }

            List<GroupMember> members = group.getMembers();
            Account account1 = accountDAO.select("", "id", group.getCreatedBy());
            members.add(new GroupMember(group, account1, Role.ADMIN));
            // moderator of the group
            String email2 = "dari@tat.ru";
            Account account2 = accountDAO.select("", EMAIL, email2);
            if (account2 == null) {
                account2 = new Account("Darina", "Sabitova", "darisabitova",
                        dao.formatter.parse("2007-01-12"), email2);
                account2.setGender(Gender.F);
                account2.setPasswordHash(account2.hashPassword("ComplicatedPassword_3"));
                accountDAO.insert("", account2);
                account2 = accountDAO.select("", EMAIL, email2);
            }
            members.add(new GroupMember(group, account2, Role.MEMBER));
            Group dbGroup = dao.insert("", members);
            assertEquals(members.size(), dbGroup.getMembers().size());
            System.out.println("Added members: " + account1 + " and " + account2);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
    }

    @Order(3)
    @Test
    void select() {
        System.out.println("---------------------------------");
        System.out.println("Test GroupDAO.select()");
        String title = "Figure skating";
        Group group = dao.select("", TITLE, title);
        assertNotNull(group);
        assertEquals(title, group.getTitle());
    }

    @Order(4)
    @Test
    void update() {
        System.out.println("---------------------------------");
        System.out.println("Test GroupDAO.update()");
        String title = "Figure skating";
        Group group = dao.select("", TITLE, title);
        assertNotNull(group);
        // updating a field
        String valueToChange = "Figure skating fans group 2023";
        Group dbGroup = dao.update("", "metaTitle", valueToChange, group);
        assertEquals(valueToChange, dbGroup.getMetaTitle());

        // updating members via query
        AccountDao accountDAO = new AccountDao(con);
        String email = "dari@tat.ru";
        Account account = accountDAO.select("", EMAIL, email);
        assertNotNull(account);
        String query = "Group_member SET roleType='moder' WHERE groupId=" + group.getId()
                + " AND accId=" + account.getId() + ";";
        dbGroup = dao.update(query, "", "", group);
        assertEquals(Role.MODER, Objects.requireNonNull(dbGroup.getMembers().stream().filter(member ->
                Objects.equals(member.getAccount().getEmail(), email)).findAny().orElse(null)).getRole());
        System.out.println("Updated group " + group);
    }

    @Order(5)
    @Test
    void deleteMembers() {
        System.out.println("---------------------------------");
        System.out.println("Test GroupDAO.delete(Members)");
        String title = "Figure skating";
        Group group = dao.select("", TITLE, title);
        assertNotNull(group);

        List<GroupMember> members = group.getMembers();
        int initialQuantity = members.size();
        members.clear();
        AccountDao accountDAO = new AccountDao(con);
        String email = "dari@tat.ru";
        Account account = accountDAO.select("", EMAIL, email);
        assertNotNull(account);
        members.add(new GroupMember(group, account, Role.MODER));
        Group dbGroup = dao.delete("", members);
        assertEquals(initialQuantity - 1, dbGroup.getMembers().size());
        System.out.println("Deleted member " + account);
    }

    @Order(6)
    @Test
    void delete() {
        System.out.println("---------------------------------");
        System.out.println("Test GroupDAO.delete()");
        String title = "Figure skating";
        Group group = dao.select("", TITLE, title);
        assertNotNull(group);

        Group dbGroup = dao.delete("", group);
        assertNull(dbGroup);
        System.out.println("Deleted group " + group);
    }

}