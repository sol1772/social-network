package com.getjavajob.training.maksyutovs.socialnetwork.service;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.AccountDao;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Friend;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountDao mockDao;
    private AccountService accountService;

    @BeforeEach
    void initService() {
        accountService = new AccountService(mockDao);
    }

    @Test
    void registerAccount() {
        Account account = new Account();
        when(mockDao.insert(any(Account.class))).then(returnsFirstArg());
        Account savedAccount = accountService.registerAccount(account);
        assertNotNull(savedAccount);
    }

    @Test
    void getAccountByEmail() {
        String email = "info@valievakamila.ru";
        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
                LocalDate.parse("2006-04-26", Utils.DATE_FORMATTER), email);
        when(mockDao.select(eq("email"), anyString())).thenReturn(account);
        Account savedAccount = accountService.getAccountByEmail(account.getEmail());
        assertEquals(email, savedAccount.getEmail());
    }

    @Test
    void editAccount() {
        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
                LocalDate.parse("2006-04-26", Utils.DATE_FORMATTER), "info@valievakamila.ru");
        account.setAddInfo("Some info");
        when(mockDao.selectByEmail(anyString())).thenReturn(true);
        when(mockDao.update(anyString(), anyString(), any(Account.class))).thenReturn(account);
        Account savedAccount = accountService.editAccount(account, "addInfo", "New info");
        assertEquals(account.getAddInfo(), savedAccount.getAddInfo());
    }

    @Test
    void deleteAccount() {
        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
                LocalDate.parse("2006-04-26", Utils.DATE_FORMATTER), "info@valievakamila.ru");
        when(mockDao.selectByEmail(anyString())).thenReturn(true);
        when(mockDao.delete(any(Account.class))).thenReturn(null);
        Account deletedAccount = accountService.deleteAccount(account);
        assertNull(deletedAccount);
    }

    @Test
    void addFriend() {
        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
                LocalDate.parse("2006-04-26", Utils.DATE_FORMATTER), "info@valievakamila.ru");
        Account friend = new Account("Alina", "Zagitova", "alina_zagitova",
                LocalDate.parse("2002-05-18", Utils.DATE_FORMATTER), "info@alinazagitova.ru");
        List<Friend> friends = account.getFriends();
        friends.add(new Friend(account, friend));

        when(mockDao.select(eq("email"), anyString())).thenReturn(account);
        when(mockDao.insert(friends)).thenReturn(account);
        Account savedAccount = accountService.addFriend(account, friend);
        assertEquals(friends.size(), savedAccount.getFriends().size());
    }

    @Test
    void deleteFriend() {
        Account account = new Account();
        Account friend = new Account();
        when(mockDao.select("email", account.getEmail())).thenReturn(account);
        when(mockDao.delete(account.getFriends())).thenReturn(account);
        assertNotNull(accountService.deleteFriend(account, friend));
    }

    @Test
    void getFriends() {
        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
                LocalDate.parse("2006-04-26", Utils.DATE_FORMATTER), "info@valievakamila.ru");
        Account friend = new Account("Alina", "Zagitova", "alina_zagitova",
                LocalDate.parse("2002-05-18", Utils.DATE_FORMATTER), "info@alinazagitova.ru");
        List<Friend> friends = account.getFriends();
        friends.add(new Friend(account, friend));

        when(mockDao.select(anyString(), anyString())).thenReturn(account);
        assertEquals(1, accountService.getFriends(account).size());
    }

    @Test
    void passwordIsValid() {
        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
                LocalDate.parse("2006-04-26", Utils.DATE_FORMATTER), "info@valievakamila.ru");
        String password = "ComplicatedPassword_1";
        account.setPasswordHash(account.hashPassword(password));

        when(mockDao.selectByEmail(anyString())).thenReturn(true);
        when(mockDao.update(anyString(), anyString(), any(Account.class))).thenReturn(account);
        Account savedAccount = accountService.editAccount(account, "passwordHash", password);
        assertTrue(accountService.passwordIsValid(password, savedAccount));
    }

    @Test
    void changePassword() {
        String email = "info@valievakamila.ru";
        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
                LocalDate.parse("2006-04-26", Utils.DATE_FORMATTER), email);
        String oldPassword = "ComplicatedPassword_1";
        String newPassword = "ComplicatedPassword_2";

        when(mockDao.selectByEmail(anyString())).thenReturn(true);
        when(mockDao.update(anyString(), anyString(), any(Account.class))).thenReturn(account);
        assertTrue(accountService.changePassword(oldPassword, newPassword, account));
    }

}
