package com.getjavajob.training.maksyutovs.socialnetwork.service;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.AccountDao;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AccountService {

    private AccountDao dao;

    public AccountService() {
    }

    public AccountService(AccountDao dao) {
        this.dao = dao;
    }

    public AccountDao getDao() {
        return dao;
    }

    public void setDao(AccountDao dao) {
        this.dao = dao;
    }

    public Account getAccountByEmail(String email) {
        return dao.select("", "email", email);
    }

    public Account getAccountById(int id) {
        return dao.select("", "id", id);
    }

    public List<Account> getAccounts() {
        return dao.selectAll("");
    }

    public List<Account> getAccountsByString(String substring, int start, int total) {
        return dao.selectByString(substring, start, total);
    }

    public Account registerAccount(Account account) {
        Account dbAccount = getAccountByEmail(account.getEmail());
        if (dbAccount == null) {
            dbAccount = dao.insert("", account);
            for (Phone phone : account.getPhones()) {
                dbAccount.getPhones().add(new Phone(dbAccount, phone.getNumber(), phone.getPhoneType()));
            }
            if (!dbAccount.getPhones().isEmpty()) {
                dbAccount = dao.insert("", dbAccount.getPhones());
            }
            for (Address address : account.getAddresses()) {
                dbAccount.getAddresses().add(new Address(dbAccount, address.getAddress(), address.getAddrType()));
            }
            if (!dbAccount.getAddresses().isEmpty()) {
                dbAccount = dao.insert("", dbAccount.getAddresses());
            }
            for (Messenger messenger : account.getMessengers()) {
                dbAccount.getMessengers().add(new Messenger(dbAccount, messenger.getUsername(), messenger.getMsngrType()));
            }
            if (!dbAccount.getMessengers().isEmpty()) {
                dbAccount = dao.insert("", dbAccount.getMessengers());
            }
        }
        return dbAccount;
    }

    public Optional<Account> editAccount(Account account, String field, Object value) {
        Account dbAccount = getAccountByEmail(account.getEmail());
        if (dbAccount != null) {
            dbAccount = dao.update("", field, value, account);
        }
        return Optional.ofNullable(dbAccount);
    }

    public Optional<Account> editAccount(Account account) {
        Account dbAccount = getAccountByEmail(account.getEmail());
        if (dbAccount != null) {
            dbAccount = dao.update(account);
        }
        return Optional.ofNullable(dbAccount);
    }

    public Optional<Account> deleteAccount(Account account) {
        Account dbAccount = getAccountByEmail(account.getEmail());
        if (dbAccount != null) {
            dao.delete("", account);
        }
        return Optional.ofNullable(dbAccount);
    }

    public Optional<Account> addFriend(Account account, Account friend) {
        Account dbAccount = getAccountByEmail(account.getEmail());
        if (dbAccount == null) {
            return Optional.empty();
        }
        Account dbFriend = getAccountByEmail(friend.getEmail());
        if (dbFriend == null) {
            return Optional.of(dbAccount);
        }
        List<Friend> friends = account.getFriends();
        friends.add(new Friend(account, dbFriend));
        dbAccount = dao.insert("", friends);
        return Optional.ofNullable(dbAccount);
    }

    public Optional<Account> deleteFriend(Account account, Account friend) {
        Account dbAccount = getAccountByEmail(account.getEmail());
        if (dbAccount == null) {
            return Optional.empty();
        }
        Account dbFriend = getAccountByEmail(friend.getEmail());
        if (dbFriend == null) {
            return Optional.of(dbAccount);
        }
        List<Friend> friends = account.getFriends();
        friends.clear();
        friends.add(new Friend(account, dbFriend));
        dbAccount = dao.delete("", friends);
        return Optional.ofNullable(dbAccount);
    }

    public List<Friend> getFriends(Account account) {
        return getAccountByEmail(account.getEmail()).getFriends();
    }

    public boolean passwordIsValid(String password, Account account) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.matches(password, account.getPasswordHash());
    }

    public boolean changePassword(String oldPassword, String newPassword, Account account) {
        boolean passwordChanged = false;
        if (StringUtils.isEmpty(account.getPasswordHash()) || passwordIsValid(oldPassword, account)) {
            account.setPasswordHash(account.hashPassword(newPassword));
            Account dbAccount = dao.update("", "passwordHash", account.getPasswordHash(), account);
            passwordChanged = Objects.equals(dbAccount.getPasswordHash(), account.getPasswordHash());
        } else {
            System.out.println("Old password is not valid!");
        }
        return passwordChanged;
    }

}
