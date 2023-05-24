package com.getjavajob.training.maksyutovs.socialnetwork.service;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.AccountDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountDao mockDao;
    private AccountService accountService;

    @BeforeEach
    void initService() {
        accountService = new AccountService();
        accountService.setDao(mockDao);
    }

    @Test
    void registerAccount() {
//        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
//                LocalDate.parse("2006-04-26", Utils.DATE_FORMATTER), "info@valievakamila.ru");
//        when(mockDao.insert("", account)).thenReturn(account);
//        Account savedAccount = accountService.registerAccount(account);
//        assertNotNull(savedAccount);
//        assertEquals(account, savedAccount);
    }

    @Test
    void getAccountByEmail() {
//        String email = "info@valievakamila.ru";
//        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
//                LocalDate.parse("2006-04-26", Utils.DATE_FORMATTER), email);
//        when(mockDao.select("", "Email", email)).thenReturn(account);
//        assertEquals(account, accountService.getAccountByEmail(email));
    }

    @Test
    void editAccount() {
//        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
//                LocalDate.parse("2006-04-26", Utils.DATE_FORMATTER), "info@valievakamila.ru");
//        account.setAddInfo("Some info");
//        lenient().doThrow(NoSuchElementException.class).when(mockDao).update(
//                anyString(), anyString(), anyString(), any(Account.class));
//        Optional<Account> savedAccount = accountService.editAccount(account, "addInfo", "New info");
//        assertThrows(NoSuchElementException.class, savedAccount::get);
    }

    @Test
    void deleteAccount() {
//        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
//                LocalDate.parse("2006-04-26", Utils.DATE_FORMATTER), "info@valievakamila.ru");
//        lenient().doThrow(NoSuchElementException.class).when(mockDao).delete("", account);
//        Optional<Account> deletedAccount = accountService.deleteAccount(account);
//        assertThrows(NoSuchElementException.class, deletedAccount::get);
    }

    @Test
    void addFriend() {
//        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
//                LocalDate.parse("2006-04-26", Utils.DATE_FORMATTER), "info@valievakamila.ru");
//        Account friend = new Account("Alina", "Zagitova", "alina_zagitova",
//                LocalDate.parse("2002-05-18", Utils.DATE_FORMATTER), "info@alinazagitova.ru");
//        List<Friend> friends = account.getFriends();
//        friends.add(new Friend(account, friend));
//
//        lenient().when(mockDao.insert("", friends)).thenReturn(account);
//        Optional<Account> savedAccount = accountService.addFriend(account, friend);
//        assertThrows(NoSuchElementException.class, savedAccount::get);
    }

    @Test
    void deleteFriend() {
//        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
//                LocalDate.parse("2006-04-26", Utils.DATE_FORMATTER), "info@valievakamila.ru");
//        Account friend = new Account("Alina", "Zagitova", "alina_zagitova",
//                LocalDate.parse("2002-05-18", Utils.DATE_FORMATTER), "info@alinazagitova.ru");
//        List<Friend> friends = account.getFriends();
//        friends.add(new Friend(account, friend));
//
//        lenient().when(mockDao.delete("", friends)).thenReturn(account);
//        Optional<Account> savedAccount = accountService.deleteFriend(account, friend);
//        assertThrows(NoSuchElementException.class, savedAccount::get);
    }

    @Test
    void getFriends() {
//        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
//                LocalDate.parse("2006-04-26", Utils.DATE_FORMATTER), "info@valievakamila.ru");
//        Account friend = new Account("Alina", "Zagitova", "alina_zagitova",
//                LocalDate.parse("2002-05-18", Utils.DATE_FORMATTER), "info@alinazagitova.ru");
//        List<Friend> friends = account.getFriends();
//        friends.add(new Friend(account, friend));
//
//        lenient().when(mockDao.insert("", friends)).thenReturn(account);
//        when(mockDao.select(anyString(), anyString(), anyString())).thenReturn(account);
//        assertEquals(1, accountService.getFriends(account).size());
    }

    @Test
    void passwordIsValid() {
//        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
//                LocalDate.parse("2006-04-26", Utils.DATE_FORMATTER), "info@valievakamila.ru");
//        String password = "ComplicatedPassword_1";
//        account.setPasswordHash(account.hashPassword(password));
//        lenient().doThrow(NoSuchElementException.class).when(mockDao).update(
//                anyString(), anyString(), anyString(), any(Account.class));
//        Optional<Account> savedAccount = accountService.editAccount(account, "passwordHash", password);
//        if (savedAccount.isEmpty()) {
//            assertThrows(NoSuchElementException.class, savedAccount::get);
//        } else {
//            assertTrue(accountService.passwordIsValid(password, savedAccount.get()));
//        }
    }

    @Test
    void changePassword() {
//        String email = "info@valievakamila.ru";
//        Account account = new Account("Kamila", "Valieva", "kamila_valieva",
//                LocalDate.parse("2006-04-26", Utils.DATE_FORMATTER), email);
//        when(mockDao.select("", "Email", email)).thenReturn(account);
//        Account dbAccount = accountService.getAccountByEmail(email);
//        String oldPassword = dbAccount.getPasswordHash();
//        String newPassword = "ComplicatedPassword_1";
//        account.setPasswordHash(account.hashPassword(newPassword));
//        Optional<Account> savedAccount = accountService.editAccount(account, "passwordHash", newPassword);
//        if (savedAccount.isEmpty()) {
//            assertThrows(NoSuchElementException.class, savedAccount::get);
//        } else {
//            assertTrue(accountService.changePassword(oldPassword, newPassword, savedAccount.get()));
//        }
    }

}
