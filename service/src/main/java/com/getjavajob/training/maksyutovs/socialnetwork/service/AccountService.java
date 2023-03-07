package com.getjavajob.training.maksyutovs.socialnetwork.service;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.AccountDAO;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AccountService {

    private static AccountDAO dao;

    public AccountService() {
    }

    public static AccountDAO getDao() {
        return dao;
    }

    public static void setDao(AccountDAO dao) {
        AccountService.dao = dao;
    }

    public Account getAccountByEmail(String email) {
        return dao.select("", "Email", email);
    }

    public Account registerAccount(Account account) {
        Account dbAccount = getAccountByEmail(account.getEmail());
        if (dbAccount == null) {
            dbAccount = dao.insert("", account);
            if (!account.getPhones().isEmpty()) {
                dbAccount = dao.insert("", account.getPhones());
            }
            if (!account.getAddresses().isEmpty()) {
                dbAccount = dao.insert("", account.getAddresses());
            }
            if (!account.getMessengers().isEmpty()) {
                dbAccount = dao.insert("", account.getMessengers());
            }
            if (!account.getFriends().isEmpty()) {
                dbAccount = dao.insert("", account.getFriends());
            }
        }
        return dbAccount;
    }

    public Optional<Account> editAccount(Account account, String field, Object value) {
        Account dbAccount = getAccountByEmail(account.getEmail());
        if (dbAccount != null) {
            dbAccount = dao.update("", field, value, account);
        } else {
            System.out.println("Account with email " + account.getEmail() + " does not exist");
        }
        return Optional.ofNullable(dbAccount);
    }

    public Optional<Account> deleteAccount(Account account) {
        Account dbAccount = getAccountByEmail(account.getEmail());
        if (dbAccount != null) {
            dao.delete("", account);
        } else {
            System.out.println("Account with email " + account.getEmail() + " does not exist");
        }
        return Optional.ofNullable(dbAccount);
    }

    public Optional<Account> addFriend(Account account, Account friend) {
        Account dbAccount = getAccountByEmail(account.getEmail());
        if (dbAccount == null) {
            System.out.println("Account with email " + account.getEmail() + " does not exist");
            return Optional.ofNullable(dbAccount);
        }
        Account dbFriend = getAccountByEmail(friend.getEmail());
        if (dbFriend == null) {
            System.out.println("Account with email " + friend.getEmail() + " does not exist");
            return Optional.of(dbAccount);
        }
        List<Account.Friend> friends = account.getFriends();
        friends.add(account.new Friend(dbFriend));
        dbAccount = dao.insert("", friends);
        return Optional.ofNullable(dbAccount);
    }

    public Optional<Account> deleteFriend(Account account, Account friend) {
        Account dbAccount = getAccountByEmail(account.getEmail());
        if (dbAccount == null) {
            System.out.println("Account with email " + account.getEmail() + " does not exist");
            return Optional.ofNullable(dbAccount);
        }
        Account dbFriend = getAccountByEmail(friend.getEmail());
        if (dbFriend == null) {
            System.out.println("Account with email " + friend.getEmail() + " does not exist");
            return Optional.of(dbAccount);
        }
        List<Account.Friend> friends = account.getFriends();
        friends.clear();
        friends.add(account.new Friend(dbFriend));
        dbAccount = dao.delete("", friends);
        return Optional.ofNullable(dbAccount);
    }

    public List<Account.Friend> getFriends(Account account) {
        return getAccountByEmail(account.getEmail()).getFriends();
    }

    public boolean passwordIsValid(String password, Account account) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.matches(password, account.getPasswordHash());
    }

    public boolean changePassword(String oldPassword, String newPassword, Account account) {
        boolean passwordChanged = false;
        if (passwordIsValid(oldPassword, account)) {
            account.setPasswordHash(account.hashPassword(newPassword));
            Account dbAccount = dao.update("", "passwordHash", account.getPasswordHash(), account);
            passwordChanged = (Objects.equals(dbAccount.getPasswordHash(), account.getPasswordHash()));
        } else {
            System.out.println("Old password is not valid!");
        }
        return passwordChanged;
    }

}