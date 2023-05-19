package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.*;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountDaoTest {

    private static final String RESOURCE_NAME = "/h2.properties";
    private static final String EMAIL = "email";
    private static final String DELIMITER = "----------------------------------";
    private static final AccountDao dao = new AccountDao(RESOURCE_NAME);
    private static final ConnectionPool pool = dao.getPool();
    private static Connection con;
    private static Statement st;
    private static ResultSet rs;

    @BeforeAll
    static void initiateTables() {
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.createStatement();

            // creating tables if not exist
            String query = getQueryCreateTables();
            st.executeUpdate(query);

            // getting tables
            query = "SELECT Concat('TRUNCATE TABLE ',table_schema,'.',TABLE_NAME, ';') AS QueryText " +
                    "FROM INFORMATION_SCHEMA.TABLES where table_schema = 'PUBLIC'";
            rs = st.executeQuery(query);
            List<String> queries = new ArrayList<>();
            while (rs.next()) {
                queries.add(rs.getObject("QueryText").toString());
            }

            // truncating tables
            for (String q : queries) {
                st.executeUpdate(q);
            }
            con.commit();
            if (queries.size() == 0) {
                System.out.println("No tables to truncate");
            } else {
                System.out.println("All tables truncated before inserting new records");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            rollbackTransaction(con);
        } finally {
            pool.returnConnection(con);
        }
    }

    static String getQueryCreateTables() {
        return "CREATE TABLE if not exists Account (" +
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
                "); " +
                "CREATE TABLE if not exists Phone (" +
                "    id INT AUTO_INCREMENT," +
                "    accId INT," +
                "    phoneNmr VARCHAR (15) NOT NULL," +
                "    phoneType ENUM('PERSONAL', 'WORK')," +
                "    PRIMARY KEY(id)," +
                "    CONSTRAINT Uq_phones UNIQUE (accId, phoneNmr, phoneType)" +
                "); " +
                "CREATE TABLE if not exists Address (" +
                "    id INT AUTO_INCREMENT," +
                "    accId INT," +
                "    addr VARCHAR (100) NOT NULL," +
                "    addrType ENUM('HOME', 'WORK')," +
                "    PRIMARY KEY(id)," +
                "    CONSTRAINT Uq_addresses UNIQUE (accId, addr, addrType)" +
                "); " +
                "CREATE TABLE if not exists Messenger (" +
                "    id INT AUTO_INCREMENT," +
                "    accId INT," +
                "    username VARCHAR (50) NOT NULL," +
                "    msngrType ENUM('SKYPE', 'TELEGRAM', 'WHATSAPP', 'ICQ')," +
                "    PRIMARY KEY(id)," +
                "    CONSTRAINT Uq_messengers UNIQUE (accId, username, msngrType)" +
                "); " +
                "CREATE TABLE if not exists Friend (" +
                "    id INT AUTO_INCREMENT," +
                "    accId INT NOT NULL," +
                "    friendID INT NOT NULL," +
                "    PRIMARY KEY(id)," +
                "    CONSTRAINT Uq_friends UNIQUE (accId, friendID)" +
                "); " +
                "CREATE TABLE if not exists InterestGroup (" +
                "    id INT AUTO_INCREMENT," +
                "    createdBy INT, " +
                "    title VARCHAR(50) NOT NULL UNIQUE, " +
                "    metaTitle VARCHAR(100), " +
                "    createdAt DATETIME," +
                "    image BLOB," +
                "    PRIMARY KEY(id)" +
                "); " +
                "CREATE TABLE if not exists Group_member (" +
                "    id INT AUTO_INCREMENT," +
                "    accId INT NOT NULL," +
                "    groupId INT NOT NULL, " +
                "    roleType ENUM('ADMIN', 'MODER', 'MEMBER')," +
                "    PRIMARY KEY(id)," +
                "    CONSTRAINT Uq_members UNIQUE (accId, groupId)" +
                ");" +
                "CREATE TABLE if not exists Message (" +
                "    id INT AUTO_INCREMENT," +
                "    accId INT," +
                "    trgtId INT," +
                "    txtContent VARCHAR (500) NOT NULL," +
                "    mediaContent BLOB," +
                "    msgType ENUM('PERSONAL', 'PUBLIC', 'GROUP')," +
                "    createdAt DATETIME NOT NULL," +
                "    updatedAt DATETIME," +
                "    PRIMARY KEY(id)" +
                ");";
    }

    @AfterAll
    static void closeConnection() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (st != null) {
                st.close();
            }
            if (con != null) {
                pool.returnConnection(con);
            }
            pool.closeConnections();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void rollbackTransaction(Connection con) {
        if (con != null) {
            try {
                System.err.print("Transaction is being rolled back");
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Order(1)
    @Test
    void insert() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.insert(Account)");
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            Account account = new Account("Kamila", "Valieva", "kamila_valieva",
                    dao.formatter.parse("2006-04-26"), "info@valievakamila.ru");
            account.setMiddleName("Valerievna");
            account.setGender(Gender.F);
            account.setAddInfo("some info");
            account.setPasswordHash(account.hashPassword("ComplicatedPassword_1"));
            Account dbAccount = dao.insert("", account);
            assertNotNull(dbAccount);
            con.commit();
            System.out.println("Created account " + account);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
            rollbackTransaction(con);
        }
    }

    @Order(2)
    @Test
    void insertContacts() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.insert(Contacts)");
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            String email = "info@valievakamila.ru";
            Account account = dao.select("", EMAIL, email);
            assertNotNull(account);

            Account dbAccount;
            List<Phone> phones = account.getPhones();
            phones.add(new Phone(account, "+79161234567", PhoneType.PERSONAL));
            phones.add(new Phone(account, "+74951234567", PhoneType.WORK));
            dbAccount = dao.insert("", phones);
            assertEquals(phones.size(), dbAccount.getPhones().size());

            List<Address> addresses = account.getAddresses();
            addresses.add(new Address(account, "Kazan", AddressType.HOME));
            addresses.add(new Address(account, "Moscow", AddressType.WORK));
            dbAccount = dao.insert("", addresses);
            assertEquals(addresses.size(), dbAccount.getAddresses().size());

            List<Messenger> messengers = account.getMessengers();
            messengers.add(new Messenger(account, "kamilavalieva", MessengerType.SKYPE));
            messengers.add(new Messenger(account, "@kamilavalieva26official", MessengerType.TELEGRAM));
            dbAccount = dao.insert("", messengers);
            assertEquals(messengers.size(), dbAccount.getMessengers().size());
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            rollbackTransaction(con);
        }
    }

    @Order(3)
    @Test
    void insertFriends() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.insert(Friends)");
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            String email = "info@valievakamila.ru";
            Account account = dao.select("", EMAIL, email);
            assertNotNull(account);
            String email2 = "info@alinazagitova.ru";
            Account friendAccount = dao.select("", EMAIL, email2);
            if (friendAccount == null) {
                friendAccount = new Account("Alina", "Zagitova", "alina_zagitova",
                        dao.formatter.parse("2002-05-18"), email2);
                friendAccount.setGender(Gender.F);
                friendAccount.setPasswordHash(account.hashPassword("ComplicatedPassword_2"));
                dao.insert("", friendAccount);
                friendAccount = dao.select("", EMAIL, email2);
                System.out.println("Created friend account " + friendAccount);
            }
            List<Friend> friends = account.getFriends();
            friends.add(new Friend(account, friendAccount));
            Account dbAccount = dao.insert("", friends);
            assertEquals(friends.size(), dbAccount.getFriends().size());
            con.commit();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
            rollbackTransaction(con);
        }
    }

    @Order(4)
    @Test
    void insertMessages() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.insert(Messages)");
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            String email = "info@valievakamila.ru";
            Account account = dao.select("", EMAIL, email);
            assertNotNull(account);
            String email2 = "info@alinazagitova.ru";
            Account targetAccount = dao.select("", EMAIL, email2);
            if (targetAccount == null) {
                targetAccount = new Account("Alina", "Zagitova", "alina_zagitova",
                        dao.formatter.parse("2002-05-18"), email2);
                targetAccount.setGender(Gender.F);
                dao.insert("", targetAccount);
                targetAccount = dao.select("", EMAIL, email2);
                System.out.println("Created target account " + targetAccount);
            }
            List<Message> messages = account.getMessages();
            messages.add(new Message(account, targetAccount, MessageType.PERSONAL, "Hello! How are you?"));
            Account dbAccount = dao.insert("", messages);
            assertEquals(messages.size(), dbAccount.getMessages().size());
            con.commit();
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            rollbackTransaction(con);
        }
    }

    @Order(5)
    @Test
    void select() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.select()");
        String email = "info@valievakamila.ru";
        Account account = dao.select("", EMAIL, email);
        assertNotNull(account);
        assertEquals(email, account.getEmail());
    }

    @Order(6)
    @Test
    void update() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.update()");
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            String email = "info@valievakamila.ru";
            Account account = dao.select("", EMAIL, email);
            assertNotNull(account);
            // updating a field
            String valueToChange = "Some info about Kamila Valieva";
            Account dbAccount = dao.update("", "addInfo", valueToChange, account);
            assertEquals(valueToChange, dbAccount.getAddInfo());

            // updating contacts via query
            String query = "PHONE SET phoneNmr='+79876543210' WHERE ACCID=" + account.getId()
                    + " AND phoneType='personal';";
            dbAccount = dao.update(query, "", "", account);
            List<Phone> phones = dbAccount.getPhones();
            assertEquals("+79876543210", Objects.requireNonNull(phones.stream().filter(phone ->
                    PhoneType.PERSONAL.equals(phone.getPhoneType())).findAny().orElse(null)).getNumber());
            con.commit();
            System.out.println("Updated account " + account);
        } catch (SQLException e) {
            e.printStackTrace();
            rollbackTransaction(con);
        }
    }

    @Order(7)
    @Test
    void deleteContacts() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.delete(Contacts)");
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            String email = "info@valievakamila.ru";
            Account account = dao.select("", EMAIL, email);
            assertNotNull(account);

            List<Phone> phones = account.getPhones();
            phones.clear();
            phones.add(new Phone(account, "+74951234567", PhoneType.WORK));
            Account dbAccount = dao.delete("", phones);
            assertEquals(1, dbAccount.getPhones().size());
            con.commit();
            System.out.println("Deleted contact");
        } catch (SQLException e) {
            e.printStackTrace();
            rollbackTransaction(con);
        }
    }

    @Order(8)
    @Test
    void deleteFriends() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.delete(Friends)");
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            String email = "info@valievakamila.ru";
            Account account = dao.select("", EMAIL, email);
            assertNotNull(account);
            String email2 = "info@alinazagitova.ru";
            Account friend = dao.select("", EMAIL, email2);
            assertNotNull(friend);

            List<Friend> friends = account.getFriends();
            int initialQuantity = friends.size();
            friends.clear();
            friends.add(new Friend(account, friend));
            Account dbAccount = dao.delete("", friends);
            assertEquals(initialQuantity - 1, dbAccount.getFriends().size());
            con.commit();
            System.out.println("Deleted friend");
        } catch (SQLException e) {
            e.printStackTrace();
            rollbackTransaction(con);
        }
    }

    @Order(9)
    @Test
    void delete() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.delete(Account)");
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            String email = "info@valievakamila.ru";
            Account account = dao.select("", EMAIL, email);
            assertNotNull(account);

            Account dbAccount = dao.delete("", account);
            assertNull(dbAccount);
            con.commit();
            System.out.println("Deleted account " + account);
        } catch (SQLException e) {
            e.printStackTrace();
            rollbackTransaction(con);
        }
    }

}