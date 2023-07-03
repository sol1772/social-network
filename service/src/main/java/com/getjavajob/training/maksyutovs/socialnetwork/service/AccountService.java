package com.getjavajob.training.maksyutovs.socialnetwork.service;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.AccountDao;
import com.getjavajob.training.maksyutovs.socialnetwork.dao.DaoRuntimeException;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class AccountService {

    private static final String EMAIL = "email";
    private AccountDao dao;

    public AccountService() {
    }

    @Autowired
    public AccountService(AccountDao dao) {
        this.dao = dao;
    }

    public AccountDao getDao() {
        return dao;
    }

    public void setDao(AccountDao dao) {
        this.dao = dao;
    }

    public Account validateAccount(Account account) {
        Account dbAccount = dao.select(EMAIL, account.getEmail());
        if (dbAccount == null) {
            throw new ValidationRuntimeException("Account with email '" + account.getEmail() + "' does not exist");
        }
        return dbAccount;
    }

    public boolean checkAccount(Account account) {
        if (!dao.selectByEmail(account.getEmail())) {
            throw new ValidationRuntimeException("Account with email '" + account.getEmail() + "' does not exist");
        }
        return true;
    }

    public Account getAccountByEmail(String email) {
        return dao.select(EMAIL, email);
    }

    public Account getAccountById(int id) {
        return dao.select("id", id);
    }

    public List<Account> getAccounts() {
        return dao.selectAll();
    }

    public List<Account> getAccountsByString(String substring, int start, int total) {
        return dao.selectByString(substring, start, total);
    }

    public int getAccountsCountByString(String substring, int start, int total) {
        return dao.selectCountByString(substring, start, total);
    }

    public List<Account> getTargetAccounts(Account account, MessageType type) {
        return dao.selectTargetAccounts(account, type);
    }

    public List<Message> getMessages(Account account, Account targetAccount, MessageType type) {
        return dao.selectMessages(account, targetAccount, type);
    }

    public List<Friend> getFriends(Account account) {
        return dao.select(EMAIL, account.getEmail()).getFriends();
    }

    @Transactional
    public boolean sendMessage(Message message) {
        return dao.insertMessage(message);
    }

    @Transactional
    public boolean deleteMessage(int id) {
        return dao.deleteMessageById(id);
    }

    @Transactional
    public Account registerAccount(Account account) {
        Account dbAccount;
        try {
            dbAccount = dao.insert(account);
            dbAccount = addAccountData(account, dbAccount);
        } catch (DaoRuntimeException e) {
            throw new DaoRuntimeException("Account already exists", e);
        }
        return dbAccount;
    }

    @Transactional
    public Account addAccountData(Account account, Account dbAccount) {
        if (!account.getPhones().isEmpty()) {
            for (Phone phone : account.getPhones()) {
                dbAccount.getPhones().add(new Phone(dbAccount, 0, phone.getNumber(), phone.getPhoneType()));
            }
            dbAccount = dao.insert(dbAccount.getPhones());
        }
        if (!account.getAddresses().isEmpty()) {
            for (Address address : account.getAddresses()) {
                dbAccount.getAddresses().add(
                        new Address(dbAccount, 0, address.getAddr(), address.getAddrType()));
            }
            dbAccount = dao.insert(dbAccount.getAddresses());
        }
        if (!account.getMessengers().isEmpty()) {
            for (Messenger messenger : account.getMessengers()) {
                dbAccount.getMessengers().add(
                        new Messenger(dbAccount, 0, messenger.getUsername(), messenger.getMsgrType()));
            }
            dbAccount = dao.insert(dbAccount.getMessengers());
        }
        return dbAccount;
    }

    @Transactional
    public <T> Account addAccountData(Account account, String value, T type) {
        Account dbAccount = null;
        if (checkAccount(account)) {
            dbAccount = dao.insert(account, value, type);
        }
        return dbAccount;
    }

    @Transactional
    public Account editAccount(Account account, String field, Object value) {
        Account dbAccount = null;
        if (checkAccount(account)) {
            dbAccount = dao.update(field, value, account);
        }
        return dbAccount;
    }

    @Transactional
    public Account editAccount(Account account) {
        Account dbAccount = null;
        if (checkAccount(account)) {
            dbAccount = dao.update(account);
        }
        return dbAccount;
    }

    @Transactional
    public <T> Account editAccountData(String value, T type, int id, Account account) {
        Account dbAccount = null;
        if (checkAccount(account)) {
            dbAccount = dao.updateAccountData(value, type, id, account);
        }
        return dbAccount;
    }

    @Transactional
    public Account deleteAccount(Account account) {
        Account dbAccount = null;
        if (checkAccount(account)) {
            dbAccount = dao.delete(account);
        }
        return dbAccount;
    }

    @Transactional
    public <T> Account deleteAccountData(T type, int id, Account account) {
        Account dbAccount = null;
        if (checkAccount(account)) {
            dbAccount = dao.deleteAccountDataById(type, id, account);
        }
        return dbAccount;
    }

    @Transactional
    public <T> boolean deleteAccountData(T type, int id) {
        return dao.deleteAccountDataById(type, id);
    }

    @Transactional
    public Account addFriend(Account account, Account friend) {
        Account dbAccount = validateAccount(account);
        Account dbFriend = validateAccount(friend);
        List<Friend> friends = dbAccount.getFriends();
        friends.add(new Friend(dbAccount, dbFriend));
        return dao.insert(friends);
    }

    @Transactional
    public Account deleteFriend(Account account, Account friend) {
        Account dbAccount = validateAccount(account);
        Account dbFriend = validateAccount(friend);
        List<Friend> friends = dbAccount.getFriends();
        friends.clear();
        friends.add(new Friend(dbAccount, dbFriend));
        return dao.delete(friends);
    }

    public boolean passwordIsValid(String password, Account account) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.matches(password, account.getPasswordHash());
    }

    @Transactional
    public boolean changePassword(String oldPassword, String newPassword, Account account) {
        boolean passwordChanged = false;
        if (checkAccount(account)) {
            if (StringUtils.isEmpty(account.getPasswordHash()) || passwordIsValid(oldPassword, account)) {
                account.setPasswordHash(account.hashPassword(newPassword));
                Account dbAccount = dao.update("passwordHash", account.getPasswordHash(), account);
                passwordChanged = Objects.equals(dbAccount.getPasswordHash(), account.getPasswordHash());
            } else {
                throw new DaoRuntimeException("Old password is not valid!");
            }
        }
        return passwordChanged;
    }

}
