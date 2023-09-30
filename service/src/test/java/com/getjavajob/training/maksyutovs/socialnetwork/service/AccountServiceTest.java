package com.getjavajob.training.maksyutovs.socialnetwork.service;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.AccountDao;
import com.getjavajob.training.maksyutovs.socialnetwork.dao.FriendDao;
import com.getjavajob.training.maksyutovs.socialnetwork.dao.MessageDao;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Friend;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Utils;
import com.getjavajob.training.maksyutovs.socialnetwork.repositories.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountDao accountDao;
    @Mock
    private FriendDao friendDao;
    @Mock
    private MessageDao messageDao;
    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void initService() {
        accountService = new AccountService(accountRepository, accountDao, friendDao, messageDao);
    }

    @Test
    void registerAccount() {
        Account account = new Account();
        when(accountRepository.save(any(Account.class))).then(returnsFirstArg());
        Account savedAccount = accountService.registerAccount(account);
        assertNotNull(savedAccount);
    }

    @Test
    void getAccountByEmail() {
        String email = "info@valievakamila.ru";
        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
                LocalDate.parse("2006-04-26", Utils.DATE_FORMATTER), email);
        when(accountRepository.findByEmail(anyString())).thenReturn(account);
        Account savedAccount = accountService.getAccountByEmail(account.getEmail());
        assertEquals(email, savedAccount.getEmail());
    }

    @Test
    void editAccount() {
        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
                LocalDate.parse("2006-04-26", Utils.DATE_FORMATTER), "info@valievakamila.ru");
        account.setAddInfo("Some info");
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        Account savedAccount = accountService.editAccount(account);
        assertEquals(account.getAddInfo(), savedAccount.getAddInfo());
    }

    @Test
    void deleteAccount() {
        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
                LocalDate.parse("2006-04-26", Utils.DATE_FORMATTER), "info@valievakamila.ru");
        account.setId(1);
        doNothing().when(accountRepository).deleteById(account.getId());
        assertTrue(accountService.deleteAccount(account.getId()));
    }

    @Test
    void addFriend() {
        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
                LocalDate.parse("2006-04-26", Utils.DATE_FORMATTER), "info@valievakamila.ru");
        Account friendAccount = new Account("Alina", "Zagitova", "alina_zagitova",
                LocalDate.parse("2002-05-18", Utils.DATE_FORMATTER), "info@alinazagitova.ru");
        when((accountRepository.findByEmail(account.getEmail()))).thenReturn(account);
        when((accountRepository.findByEmail(friendAccount.getEmail()))).thenReturn(friendAccount);
        Friend friend = new Friend(account, friendAccount);
        when(friendDao.insert(friend)).thenReturn(friend);
        Friend savedFriend = accountService.addFriend(account, friendAccount);
        assertNotNull(savedFriend);
    }

    @Test
    void deleteFriend() {
        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
                LocalDate.parse("2006-04-26", Utils.DATE_FORMATTER), "info@valievakamila.ru");
        Account friendAccount = new Account("Alina", "Zagitova", "alina_zagitova",
                LocalDate.parse("2002-05-18", Utils.DATE_FORMATTER), "info@alinazagitova.ru");
        when((accountRepository.findByEmail(account.getEmail()))).thenReturn(account);
        when((accountRepository.findByEmail(friendAccount.getEmail()))).thenReturn(friendAccount);
        Friend friend = new Friend(account, friendAccount);
        when((friendDao.selectFriend(account, friendAccount))).thenReturn(friend);
        when(accountDao.delete(friend)).thenReturn(true);
        assertTrue(accountService.deleteFriend(account, friendAccount));
    }

    @Test
    void passwordIsValid() {
        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
                LocalDate.parse("2006-04-26", Utils.DATE_FORMATTER), "info@valievakamila.ru");
        String password = "ComplicatedPassword_1";
        account.setPasswordHash(account.hashPassword(password));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        Account savedAccount = accountService.editAccount(account);
        assertTrue(accountService.passwordIsValid(password, savedAccount));
    }

    @Test
    void changePassword() {
        String email = "info@valievakamila.ru";
        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
                LocalDate.parse("2006-04-26", Utils.DATE_FORMATTER), email);
        String oldPassword = "ComplicatedPassword_1";
        String newPassword = "ComplicatedPassword_2";
        when(accountRepository.findByEmail(anyString())).thenReturn(account);
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        assertTrue(accountService.changePassword(oldPassword, newPassword, account));
    }

}
