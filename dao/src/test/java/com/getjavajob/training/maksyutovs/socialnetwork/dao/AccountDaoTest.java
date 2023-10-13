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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = DaoTestConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = "/truncate_tables.sql", executionPhase = BEFORE_TEST_METHOD)
@Transactional
class AccountDaoTest {

    private static final String DELIMITER = "----------------------------------";
    @Autowired
    @Qualifier("accountDao")
    private CrudDao<Account> dao;
    @Autowired
    @Qualifier("friendDao")
    private CrudDao<Friend> friendDao;
    @Autowired
    @Qualifier("messageDao")
    private CrudDao<Message> messageDao;
    @Autowired
    private DataSource dataSource;

    @BeforeAll
    void init() throws SQLException {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.beforeAll");
        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("create_tables.sql"));
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
        messageDao.insert(message);
        assertEquals(1, messageDao.findAll().size());
        System.out.println("Added " + messageDao.findAll().size() + " message(s) for account " + account);
    }

    @Test
    void select() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountDAO.select()");
        String email = "info@valievakamila.ru";
        AccountDao accountDao = (AccountDao) DaoTestConfig.unProxyBean(dao);
        Account account = accountDao.selectByEmail(email);
        assertNull(account);
        account = dao.insert(getNewAccount());
        assertNotNull(account);
        assertEquals(account, accountDao.selectById(account.getId()));
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
