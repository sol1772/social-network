package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:dao-context.xml", "classpath:dao-test-context.xml"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class AccountDaoTest {

    private static final Logger logger = LoggerFactory.getLogger(AccountDaoTest.class);
    private static final String DELIMITER = "----------------------------------";
    @Autowired
    private AccountDao dao;
    @Autowired
    private FriendDao friendDao;
    @Autowired
    private MessageDao messageDao;
    private EntityManager em;

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
        em = Persistence.createEntityManagerFactory("test").createEntityManager();
        createTablesIfNotExist();
    }

    @BeforeEach
    void truncate() {
        truncateTables();
    }

    void createTablesIfNotExist() {
        String query = getQueryCreateTables();
        try {
            em.getTransaction().begin();
            em.createNativeQuery(query).executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
    }

    @SuppressWarnings("unchecked")
    void truncateTables() {
        try {
            // getting tables
            em.getTransaction().begin();
            String query = "SELECT Concat('TRUNCATE TABLE ',table_schema,'.',TABLE_NAME, ';') AS QueryText " +
                    "FROM INFORMATION_SCHEMA.TABLES where table_schema = 'PUBLIC'";
            List<String> queries = dao.em.createNativeQuery(query).getResultList();

            // truncating tables
            for (String q : queries) {
                em.createNativeQuery(q).executeUpdate();
            }
            em.getTransaction().commit();
            if (logger.isInfoEnabled()) {
                if (queries.size() == 0) {
                    logger.info("No tables to truncate");
                } else {
                    logger.info("All tables truncated");
                }
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
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
        Account dbAccount = dao.insert(getNewAccount());
        assertNotNull(dbAccount);
        System.out.println("Created account " + dbAccount);
    }

    @Test
    void insertContacts() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.insert(Contacts)");
        Account account = getNewAccount();
        account.getPhones().add(new Phone(account, 0, "+79161234567", PhoneType.PERSONAL));
        account.getPhones().add(new Phone(account, 0, "+74951234567", PhoneType.WORK));
        account.getAddresses().add(new Address(account, 0, "Kazan", AddressType.HOME));
        account.getAddresses().add(new Address(account, 0, "Moscow", AddressType.WORK));
        account.getMessengers().add(new Messenger(account, 0, "kamilavalieva", MessengerType.SKYPE));
        account.getMessengers().add(new Messenger(account, 0, "@kamilavalieva26official", MessengerType.TELEGRAM));

        Account dbAccount = dao.insert(account);
        assertNotNull(dbAccount);
        assertEquals(account.getPhones().size(), dbAccount.getPhones().size());
        assertEquals(account.getAddresses().size(), dbAccount.getAddresses().size());
        assertEquals(account.getMessengers().size(), dbAccount.getMessengers().size());
        System.out.println("Added contacts for account " + dbAccount);
    }

    @Test
    void insertFriend() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.insert(Friends)");
        Account account = getNewAccount();
        Account friendAccount = getNewTargetAccount();
        assertNotNull(dao.insert(account));
        assertNotNull(dao.insert(friendAccount));
        Friend friend = new Friend(account, friendAccount);
        Friend dbFriend = friendDao.insert(friend);
        assertNotNull(dbFriend);
        assertEquals(1, friendDao.findAll().size());
        System.out.println("Added friend " + friendAccount + " for account " + account);
    }

    @Test
    void insertMessage() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.insert(Messages)");
        Account account = getNewAccount();
        Account targetAccount = getNewTargetAccount();
        assertNotNull(dao.insert(account));
        assertNotNull(dao.insert(targetAccount));
        Message message = new Message(account, targetAccount, MessageType.PERSONAL, "Hello! How are you?");
        Message dbMessage = messageDao.insert(message);
        assertEquals(1, messageDao.findAll().size());
        System.out.println("Added " + messageDao.findAll().size() + " message(s) for account " + account);
    }

    @Test
    void select() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.select()");
        String email = "info@valievakamila.ru";
        Account account = dao.selectByEmail(email);
        assertNull(account);
        account = dao.insert(getNewAccount());
        assertNotNull(account);
        assertEquals(account, dao.selectById(account.getId()));
    }

    @Test
    void selectByString() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.selectByString()");
        String str = "mil";
        assertEquals(0, dao.selectByString(str, 1, 5).size());
        Account account = dao.insert(getNewAccount());
        assertNotNull(account);
        assertEquals(1, dao.selectByString(str, 1, 5).size());
    }

    @Test
    void update() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.update()");
        Account account = dao.insert(getNewAccount());
        assertNotNull(account);
        // updating a field
        String valueToChange = "Some info about Kamila Valieva";
        account.setAddInfo(valueToChange);
        Account dbAccount = dao.update(account);
        assertEquals(valueToChange, dbAccount.getAddInfo());
        System.out.println("Updated field 'addInfo' of account " + dbAccount);
    }

    @Test
    void delete() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.delete(Account)");
        Account account = dao.insert(getNewAccount());
        assertNotNull(account);

        assertTrue(dao.delete(account.getId()));
        System.out.println("Deleted account " + account);
    }

}
