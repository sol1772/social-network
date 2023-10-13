package com.getjavajob.training.maksyutovs.socialnetwork.repositories;

import com.getjavajob.training.maksyutovs.socialnetwork.DaoTestConfig;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Gender;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Utils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
class AccountRepositoryTest {

    private static final String DELIMITER = "----------------------------------";
    @Autowired
    private AccountRepository accountRepository;
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
    void save() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountRepository.save(Account)");
        Account dbAccount = accountRepository.save(getNewAccount());
        assertNotNull(dbAccount);
        System.out.println("Saved account " + dbAccount);
    }

    @Test
    void update() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountRepository.update()");
        Account account = accountRepository.save(getNewAccount());
        assertNotNull(account);
        // updating a field
        String valueToChange = "Some info about Kamila Valieva";
        account.setAddInfo(valueToChange);
        Account dbAccount = accountRepository.save(account);
        assertEquals(valueToChange, dbAccount.getAddInfo());
        System.out.println("Updated field 'addInfo' of account " + dbAccount);
    }

    @Test
    void findAccountById() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountRepository.findAccountById()");
        Account account = accountRepository.save(getNewAccount());
        assertNotNull(account);
        assertEquals(account, accountRepository.findAccountById(account.getId()));
    }

    @Test
    void findAccountByIdEagerly() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountRepository.findAccountByIdEagerly()");
        Account account = accountRepository.save(getNewAccount());
        assertNotNull(account);
        assertEquals(account, accountRepository.findAccountByIdEagerly(account.getId()));
    }

    @Test
    void findByEmail() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountRepository.findByEmail()");
        String email = "info@valievakamila.ru";
        Account account = accountRepository.findByEmail(email);
        assertNull(account);
        account = accountRepository.save(getNewAccount());
        assertNotNull(account);
        assertEquals(account, accountRepository.findByEmail(email));
    }

    @Test
    void findAccountByEmailEagerly() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountRepository.findAccountByEmailEagerly()");
        String email = "info@valievakamila.ru";
        Account account = accountRepository.findAccountByEmailEagerly(email);
        assertNull(account);
        account = accountRepository.save(getNewAccount());
        assertNotNull(account);
        assertEquals(account, accountRepository.findAccountByEmailEagerly(email));
    }

    @Test
    void findByFirstNameLikeOrLastNameLike() {
        System.out.println(DELIMITER);
        System.out.println("Test AccountRepository.findByFirstNameLikeOrLastNameLike()");
        Account account = getNewAccount();
        Account targetAccount = getNewTargetAccount();
        assertNotNull(accountRepository.save(account));
        assertNotNull(accountRepository.save(targetAccount));
        String searchFirstName = "%Ali%";
        String searchLastName = "%ali%";
        List<Account> accounts = accountRepository.findByFirstNameLikeOrLastNameLike(searchFirstName, searchLastName,
                PageRequest.of(0, 10, Sort.by("lastName"))).getContent();
        assertEquals(2, accounts.size());
    }

}