package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GroupDaoTest {

    private static final Logger LOGGER = Logger.getLogger(GroupDaoTest.class.getName());
    private static final String RESOURCE_NAME = "/h2.properties";
    private static final Properties properties = new Properties();
    private static final String TITLE = "title";
    private static final String EMAIL = "email";
    private static final String DELIMITER = "----------------------------------";
    private static GroupDao dao;
    private static AccountDao accountDAO;
    private static DataSourceHolder dataSourceHolder;

    @BeforeAll
    static void connect() {
        try (InputStream is = GroupDaoTest.class.getResourceAsStream(RESOURCE_NAME)) {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dao = new GroupDao(properties);
        dataSourceHolder = dao.getDataSourceHolder();
        accountDAO = new AccountDao(properties);
        initiateTables();
    }

    static void initiateTables() {
        try (Connection con = dataSourceHolder.getConnection()) {
            boolean initialAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            try (Statement st = con.createStatement()) {
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
                st.executeUpdate(query);
                // truncating tables
                query = "TRUNCATE TABLE InterestGroup";
                st.executeUpdate(query);
                query = "TRUNCATE TABLE Group_member";
                st.executeUpdate(query);
                System.out.println("'InterestGroup' and 'Group_member' tables truncated before adding a new group");
                con.commit();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
                rollbackTransaction(con);
            } finally {
                con.setAutoCommit(initialAutoCommit);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
    }

    @AfterAll
    static void closeConnection() {
        dataSourceHolder.returnConnection();
    }

    static void rollbackTransaction(Connection con) {
        if (con != null) {
            try {
                LOGGER.log(Level.WARNING, "Transaction is being rolled back");
                con.rollback();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
            }
        }
    }

    @Order(1)
    @Test
    void insert() {
        System.out.println(DELIMITER);
        System.out.println("Test GroupDAO.insert(Group)");
        try (Connection con = dataSourceHolder.getConnection()) {
            boolean initialAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                // registering new group
                Group group = new Group("Figure skating");
                group.setMetaTitle("Figure skating fans group");
                // admin of the group
                String email = "info@alinazagitova.ru";
                Account account = accountDAO.select("", EMAIL, email);
                if (account == null) {
                    account = new Account("Alina", "Zagitova", "alina_zagitova",
                            LocalDate.parse("2002-05-18", Utils.DATE_FORMATTER), email);
                    account.setGender(Gender.F);
                    accountDAO.insert("", account);
                    account = accountDAO.select("", EMAIL, email);
                }
                group.setCreatedBy(account.getId());
                Group dbGroup = dao.insert("", group);
                assertNotNull(dbGroup);
                con.commit();
                System.out.println("Created group " + group);
            } catch (DaoException | DaoRuntimeException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
                rollbackTransaction(con);
            } finally {
                con.setAutoCommit(initialAutoCommit);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
    }

    @Order(2)
    @Test
    void insertMembers() {
        System.out.println(DELIMITER);
        System.out.println("Test GroupDAO.insert(Members)");
        try (Connection con = dataSourceHolder.getConnection()) {
            boolean initialAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                Group group = dao.select("", TITLE, "Figure skating");
                if (group == null) {
                    // registering new group
                    group = new Group("Figure skating");
                    group.setMetaTitle("Figure skating fans group");
                    // admin of the group
                    String email = "info@alinazagitova.ru";
                    Account account = accountDAO.select("", EMAIL, email);
                    if (account == null) {
                        account = new Account("Alina", "Zagitova", "alina_zagitova",
                                LocalDate.parse("2002-05-18", Utils.DATE_FORMATTER), email);
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
                            LocalDate.parse("2007-01-12", Utils.DATE_FORMATTER), email2);
                    account2.setGender(Gender.F);
                    account2.setPasswordHash(account2.hashPassword("ComplicatedPassword_3"));
                    accountDAO.insert("", account2);
                    account2 = accountDAO.select("", EMAIL, email2);
                }
                members.add(new GroupMember(group, account2, Role.MEMBER));
                Group dbGroup = dao.insert("", members);
                assertEquals(members.size(), dbGroup.getMembers().size());
                con.commit();
                System.out.println("Added members: " + account1 + " and " + account2);
            } catch (DaoException | DaoRuntimeException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
                rollbackTransaction(con);
            } finally {
                con.setAutoCommit(initialAutoCommit);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
    }

    @Order(3)
    @Test
    void select() {
        System.out.println(DELIMITER);
        System.out.println("Test GroupDAO.select()");
        try (Connection ignored = dataSourceHolder.getConnection()) {
            String title = "Figure skating";
            Group group = dao.select("", TITLE, title);
            assertNotNull(group);
            assertEquals(title, group.getTitle());
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
    }

    @Order(4)
    @Test
    void update() {
        System.out.println(DELIMITER);
        System.out.println("Test GroupDAO.update()");
        try (Connection con = dataSourceHolder.getConnection()) {
            boolean initialAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                String title = "Figure skating";
                Group group = dao.select("", TITLE, title);
                assertNotNull(group);
                // updating a field
                String valueToChange = "Figure skating fans group 2023";
                Group dbGroup = dao.update("", "metaTitle", valueToChange, group);
                assertEquals(valueToChange, dbGroup.getMetaTitle());

                // updating members via query
                String email = "dari@tat.ru";
                Account account = accountDAO.select("", EMAIL, email);
                assertNotNull(account);
                String query = "Group_member SET roleType='moder' WHERE groupId=" + group.getId()
                        + " AND accId=" + account.getId() + ";";
                dbGroup = dao.update(query, "", "", group);
                assertEquals(Role.MODER, Objects.requireNonNull(dbGroup.getMembers().stream().filter(member ->
                        Objects.equals(member.getAccount().getEmail(), email)).findAny().orElse(null)).getRole());
                con.commit();
                System.out.println("Updated group " + group);
            } catch (DaoException | DaoRuntimeException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
                rollbackTransaction(con);
            } finally {
                con.setAutoCommit(initialAutoCommit);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
    }

    @Order(5)
    @Test
    void deleteMembers() {
        System.out.println(DELIMITER);
        System.out.println("Test GroupDAO.delete(Members)");
        try (Connection con = dataSourceHolder.getConnection()) {
            boolean initialAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                String title = "Figure skating";
                Group group = dao.select("", TITLE, title);
                assertNotNull(group);

                List<GroupMember> members = group.getMembers();
                int initialQuantity = members.size();
                members.clear();
                String email = "dari@tat.ru";
                Account account = accountDAO.select("", EMAIL, email);
                assertNotNull(account);
                members.add(new GroupMember(group, account, Role.MODER));
                Group dbGroup = dao.delete("", members);
                assertEquals(initialQuantity - 1, dbGroup.getMembers().size());
                con.commit();
                System.out.println("Deleted member " + account);
            } catch (DaoException | DaoRuntimeException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
                rollbackTransaction(con);
            } finally {
                con.setAutoCommit(initialAutoCommit);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
    }

    @Order(6)
    @Test
    void delete() {
        System.out.println(DELIMITER);
        System.out.println("Test GroupDAO.delete()");
        try (Connection con = dataSourceHolder.getConnection()) {
            boolean initialAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                String title = "Figure skating";
                Group group = dao.select("", TITLE, title);
                assertNotNull(group);

                Group dbGroup = dao.delete("", group);
                assertNull(dbGroup);
                con.commit();
                System.out.println("Deleted group " + group);
            } catch (DaoException | DaoRuntimeException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
                rollbackTransaction(con);
            } finally {
                con.setAutoCommit(initialAutoCommit);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
    }

}