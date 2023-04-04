package com.getjavajob.training.maksyutovs.socialnetwork.service;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.AccountDao;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Friend;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    public final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    @Mock
    private AccountDao mockDao;
    private AccountService accountService;

    @BeforeEach
    void initService() {
        accountService = new AccountService();
        accountService.setDao(mockDao);
    }

    @Test
    void registerAccount() throws ParseException {
        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
                formatter.parse("2006-04-26"), "info@valievakamila.ru");
        when(mockDao.insert("", account)).thenReturn(account);
        Account savedAccount = accountService.registerAccount(account);
        assertNotNull(savedAccount);
        assertEquals(account, savedAccount);
    }

    @Test
    void getAccountByEmail() throws ParseException {
        String email = "info@valievakamila.ru";
        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
                formatter.parse("2006-04-26"), email);
        when(mockDao.select("", "Email", email)).thenReturn(account);
        assertEquals(account, accountService.getAccountByEmail(email));
    }

    @Test
    void editAccount() throws ParseException {
        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
                formatter.parse("2006-04-26"), "info@valievakamila.ru");
        account.setAddInfo("Some info");
        lenient().doThrow(NoSuchElementException.class).when(mockDao).update(
                anyString(), anyString(), anyString(), any(Account.class));
        Optional<Account> savedAccount = accountService.editAccount(account, "addInfo", "New info");
        assertThrows(NoSuchElementException.class, savedAccount::get);
    }

    @Test
    void deleteAccount() throws ParseException {
        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
                formatter.parse("2006-04-26"), "info@valievakamila.ru");
        lenient().doThrow(NoSuchElementException.class).when(mockDao).delete("", account);
        Optional<Account> deletedAccount = accountService.deleteAccount(account);
        assertThrows(NoSuchElementException.class, deletedAccount::get);
    }

    @Test
    void addFriend() throws ParseException {
        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
                formatter.parse("2006-04-26"), "info@valievakamila.ru");
        Account friend = new Account("Alina", "Zagitova", "alina_zagitova",
                new SimpleDateFormat("yyyy-MM-dd").parse("2002-05-18"), "info@alinazagitova.ru");
        List<Friend> friends = account.getFriends();
        friends.add(new Friend(account, friend));

        lenient().when(mockDao.insert("", friends)).thenReturn(account);
        Optional<Account> savedAccount = accountService.addFriend(account, friend);
        assertThrows(NoSuchElementException.class, savedAccount::get);
    }

    @Test
    void deleteFriend() throws ParseException {
        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
                formatter.parse("2006-04-26"), "info@valievakamila.ru");
        Account friend = new Account("Alina", "Zagitova", "alina_zagitova",
                new SimpleDateFormat("yyyy-MM-dd").parse("2002-05-18"), "info@alinazagitova.ru");
        List<Friend> friends = account.getFriends();
        friends.add(new Friend(account, friend));

        lenient().when(mockDao.delete("", friends)).thenReturn(account);
        Optional<Account> savedAccount = accountService.deleteFriend(account, friend);
        assertThrows(NoSuchElementException.class, savedAccount::get);
    }

    @Test
    void getFriends() throws ParseException {
        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
                formatter.parse("2006-04-26"), "info@valievakamila.ru");
        Account friend = new Account("Alina", "Zagitova", "alina_zagitova",
                new SimpleDateFormat("yyyy-MM-dd").parse("2002-05-18"), "info@alinazagitova.ru");
        List<Friend> friends = account.getFriends();
        friends.add(new Friend(account, friend));

        lenient().when(mockDao.insert("", friends)).thenReturn(account);
        when(mockDao.select(anyString(), anyString(), anyString())).thenReturn(account);
        assertEquals(1, accountService.getFriends(account).size());
    }

    @Test
    void passwordIsValid() throws ParseException {
        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
                formatter.parse("2006-04-26"), "info@valievakamila.ru");
        String password = "ComplicatedPassword_1";
        account.setPasswordHash(account.hashPassword(password));
        lenient().doThrow(NoSuchElementException.class).when(mockDao).update(
                anyString(), anyString(), anyString(), any(Account.class));
        Optional<Account> savedAccount = accountService.editAccount(account, "passwordHash", password);
        if (savedAccount.isEmpty()) {
            assertThrows(NoSuchElementException.class, savedAccount::get);
        } else {
            assertTrue(accountService.passwordIsValid(password, savedAccount.get()));
        }
    }

    @Test
    void changePassword() throws ParseException {
        String email = "info@valievakamila.ru";
        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
                formatter.parse("2006-04-26"), email);
        when(mockDao.select("", "Email", email)).thenReturn(account);
        Account dbAccount = accountService.getAccountByEmail(email);
        String oldPassword = dbAccount.getPasswordHash();
        String newPassword = "ComplicatedPassword_1";
        account.setPasswordHash(account.hashPassword(newPassword));
        Optional<Account> savedAccount = accountService.editAccount(account, "passwordHash", newPassword);
        if (savedAccount.isEmpty()) {
            assertThrows(NoSuchElementException.class, savedAccount::get);
        } else {
            assertTrue(accountService.changePassword(oldPassword, newPassword, savedAccount.get()));
        }
    }

}
