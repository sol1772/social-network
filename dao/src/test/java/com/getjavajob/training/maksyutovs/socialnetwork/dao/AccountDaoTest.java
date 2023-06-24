package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.*;
import org.junit.jupiter.api.BeforeAll;
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
class AccountDaoTest {

    private static final Logger LOGGER = Logger.getLogger(AccountDaoTest.class.getName());
    private static final String EMAIL = "email";
    private static final String DELIMITER = "----------------------------------";
    @Autowired
    private AccountDao dao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

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
                "    msgrType ENUM('SKYPE', 'TELEGRAM', 'WHATSAPP', 'ICQ')," +
                "    PRIMARY KEY(id)," +
                "    CONSTRAINT Uq_messengers UNIQUE (accId, username, msgrType)" +
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
                "    trgId INT," +
                "    txtContent VARCHAR (500) NOT NULL," +
                "    mediaContent BLOB," +
                "    msgType ENUM('PERSONAL', 'POST', 'GROUP')," +
                "    createdAt DATETIME NOT NULL," +
                "    updatedAt DATETIME," +
                "    PRIMARY KEY(id)" +
                ");";
    }

    @BeforeAll
    void init() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.beforeAll");
        createTablesIfNotExist();
    }

    void createTablesIfNotExist() {
        String query = getQueryCreateTables();
        jdbcTemplate.execute(query);
    }

    void truncateTables() {
        // getting tables
        String query = "SELECT Concat('TRUNCATE TABLE ',table_schema,'.',TABLE_NAME, ';') AS QueryText " +
                "FROM INFORMATION_SCHEMA.TABLES where table_schema = 'PUBLIC'";
        List<String> queries = jdbcTemplate.query(query, (rs, rowNum) -> rs.getObject("QueryText").toString());

        // truncating tables
        for (String q : queries) {
            jdbcTemplate.update(q);
        }
        if (queries.size() == 0) {
            LOGGER.log(Level.CONFIG, "No tables to truncate");
        } else {
            LOGGER.log(Level.CONFIG, "All tables truncated");
        }
    }

    Account getNewAccount() {
        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
                LocalDate.parse("2006-04-26", Utils.DATE_FORMATTER), "info@valievakamila.ru");
        account.setMiddleName("Valerievna");
        account.setGender(Gender.F);
        account.setAddInfo("some info");
        account.setPasswordHash(account.hashPassword("ComplicatedPassword_1"));
        return account;
    }

    Account getNewTargetAccount() {
        Account targetAccount = new Account("Alina", "Zagitova", "alina_zagitova",
                LocalDate.parse("2002-05-18", Utils.DATE_FORMATTER), "info@alinazagitova.ru");
        targetAccount.setMiddleName("Ilnazovna");
        targetAccount.setGender(Gender.F);
        targetAccount.setAddInfo("some info");
        targetAccount.setPasswordHash(targetAccount.hashPassword("ComplicatedPassword_2"));
        return targetAccount;
    }

    @Test
    void insert() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.insert(Account)");
        truncateTables();
        Account dbAccount = dao.insert(getNewAccount());
        assertNotNull(dbAccount);
        System.out.println("Created account " + dbAccount);
    }

    @Test
    void insertContacts() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.insert(Contacts)");
        truncateTables();
        Account dbAccount = dao.insert(getNewAccount());
        assertNotNull(dbAccount);

        List<Phone> phones = dbAccount.getPhones();
        phones.add(new Phone(dbAccount, 0, "+79161234567", PhoneType.PERSONAL));
        phones.add(new Phone(dbAccount, 0, "+74951234567", PhoneType.WORK));
        dbAccount = dao.insert(phones);
        assertEquals(phones.size(), dbAccount.getPhones().size());

        List<Address> addresses = dbAccount.getAddresses();
        addresses.add(new Address(dbAccount, 0, "Kazan", AddressType.HOME));
        addresses.add(new Address(dbAccount, 0, "Moscow", AddressType.WORK));
        dbAccount = dao.insert(addresses);
        assertEquals(addresses.size(), dbAccount.getAddresses().size());

        List<Messenger> messengers = dbAccount.getMessengers();
        messengers.add(new Messenger(dbAccount, 0, "kamilavalieva", MessengerType.SKYPE));
        messengers.add(new Messenger(dbAccount, 0, "@kamilavalieva26official", MessengerType.TELEGRAM));
        dbAccount = dao.insert(messengers);
        assertEquals(messengers.size(), dbAccount.getMessengers().size());
        System.out.println("Added contacts for account " + dbAccount);
    }

    @Test
    void insertFriends() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.insert(Friends)");
        truncateTables();
        Account account = dao.insert(getNewAccount());
        assertNotNull(account);
        Account friendAccount = dao.insert(getNewTargetAccount());
        assertNotNull(friendAccount);
        List<Friend> friends = account.getFriends();
        friends.add(new Friend(account, friendAccount));
        Account dbAccount = dao.insert(friends);
        assertEquals(friends.size(), dbAccount.getFriends().size());
        System.out.println("Added friend " + friendAccount + " for account " + dbAccount);
    }

    @Test
    void insertMessages() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.insert(Messages)");
        truncateTables();
        Account account = dao.insert(getNewAccount());
        assertNotNull(account);
        Account targetAccount = dao.insert(getNewTargetAccount());
        assertNotNull(targetAccount);
        List<Message> messages = account.getMessages();
        messages.add(new Message(account, targetAccount, MessageType.PERSONAL, "Hello! How are you?"));
        Account dbAccount = dao.insert(messages);
        assertEquals(messages.size(), dbAccount.getMessages().size());
        System.out.println("Added " + dbAccount.getMessages().size() + " message(s) for account " + dbAccount);
    }

    @Test
    void select() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.select()");
        truncateTables();
        String email = "info@valievakamila.ru";
        Account account = dao.select(EMAIL, email);
        assertNull(account);
        account = dao.insert(getNewAccount());
        assertNotNull(account);
        account = dao.select(EMAIL, email);
        assertEquals(email, account.getEmail());
    }

    @Test
    void update() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.update()");
        truncateTables();
        Account account = dao.insert(getNewAccount());
        assertNotNull(account);
        // updating a field
        String valueToChange = "Some info about Kamila Valieva";
        Account dbAccount = dao.update("addInfo", valueToChange, account);
        assertEquals(valueToChange, dbAccount.getAddInfo());
        System.out.println("Updated field 'addInfo' of account " + dbAccount);

        // updating contacts
        List<Phone> phones = dbAccount.getPhones();
        phones.add(new Phone(dbAccount, 0, "+79161234567", PhoneType.PERSONAL));
        phones.add(new Phone(dbAccount, 0, "+74951234567", PhoneType.WORK));
        dbAccount = dao.insert(phones);
        Phone phone = (Phone) dao.selectByValueAndType("79161234567", PhoneType.PERSONAL, dbAccount);
        Account updatedAccount = dao.updateAccountData("+79876543210", PhoneType.PERSONAL, phone.getId(), dbAccount);
        assertEquals("+79876543210", Objects.requireNonNull(updatedAccount.getPhones().stream().filter(finalPhone ->
                PhoneType.PERSONAL.equals(finalPhone.getPhoneType())).findAny().orElse(null)).getNumber());
        System.out.println("Updated phones of account " + updatedAccount);
    }

    @Test
    void deleteContacts() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.delete(Contacts)");
        truncateTables();
        Account account = dao.insert(getNewAccount());
        assertNotNull(account);
        List<Phone> phones = account.getPhones();
        phones.add(new Phone(account, 0, "+79161234567", PhoneType.PERSONAL));
        phones.add(new Phone(account, 0, "+74951234567", PhoneType.WORK));
        Account dbAccount = dao.insert(phones);
        assertEquals(phones.size(), dbAccount.getPhones().size());

        phones = dbAccount.getPhones();
        phones.clear();
        phones.add(new Phone(account, 0, "+74951234567", PhoneType.WORK));
        Account updatedAccount = dao.delete(phones);
        assertEquals(1, updatedAccount.getPhones().size());
        System.out.println("Deleted phones of account " + updatedAccount);
    }

    @Test
    void deleteFriends() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.delete(Friends)");
        truncateTables();
        Account account = dao.insert(getNewAccount());
        assertNotNull(account);
        Account friendAccount = dao.insert(getNewTargetAccount());
        assertNotNull(friendAccount);
        List<Friend> friends = account.getFriends();
        friends.add(new Friend(account, friendAccount));
        Account dbAccount = dao.insert(friends);
        assertEquals(friends.size(), dbAccount.getFriends().size());

        friends = dbAccount.getFriends();
        int initialQuantity = friends.size();
        friends.clear();
        friends.add(new Friend(account, friendAccount));
        Account updatedAccount = dao.delete(friends);
        assertEquals(initialQuantity - 1, updatedAccount.getFriends().size());
        System.out.println("Deleted friends of account " + updatedAccount);
    }

    @Test
    void delete() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.delete(Account)");
        truncateTables();
        Account account = dao.insert(getNewAccount());
        assertNotNull(account);

        Account dbAccount = dao.delete(account);
        assertNull(dbAccount);
        System.out.println("Deleted account " + account);
    }

}
