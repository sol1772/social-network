package com.getjavajob.training.maksyutovs.socialnetwork.service;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.AccountDao;
import com.getjavajob.training.maksyutovs.socialnetwork.dao.DaoRuntimeException;
import com.getjavajob.training.maksyutovs.socialnetwork.dao.TransactionManager;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Objects;

public class AccountService {

    private static final String EMAIL = "email";
    private AccountDao dao;
    private TransactionManager transactionManager;

    public AccountService() {
    }

    public AccountService(AccountDao dao) {
        this.dao = dao;
        this.transactionManager = new TransactionManager(dao.getDataSourceHolder());
    }

    public AccountDao getDao() {
        return dao;
    }

    public void setDao(AccountDao dao) {
        this.dao = dao;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public Account getAccountByEmail(String email) {
        return transactionManager.executeAction(() -> dao.select("", EMAIL, email));
    }

    public Account getAccountById(int id) {
        return transactionManager.executeAction(() -> dao.select("", "id", id));
    }

    public List<Account> getAccounts() {
        return transactionManager.executeAction(() -> dao.selectAll(""));
    }

    public List<Account> getAccountsByString(String substring, int start, int total) {
        return transactionManager.executeAction(() -> dao.selectByString(substring, start, total));
    }

    public int getAccountsCountByString(String substring, int start, int total) {
        return transactionManager.executeAction(() -> dao.selectCountByString(substring, start, total));
    }

    public List<Account> getTargetAccounts(Account account, MessageType type) {
        return transactionManager.executeAction(() -> dao.selectTargetAccounts(account, type));
    }

    public List<Message> getMessages(Account account, Account targetAccount, MessageType type) {
        return transactionManager.executeAction(() -> dao.selectMessages(account, targetAccount, type));
    }

    public boolean sendMessage(Message message) {
        return transactionManager.executeTransaction(() -> dao.insertMessage(message));
    }

    public boolean deleteMessage(int id) {
        return transactionManager.executeTransaction(() -> dao.deleteMessageById(id));
    }

    public Account registerAccount(Account account) {
        return transactionManager.executeTransaction(() -> {
            Account dbAccount = dao.select("", EMAIL, account.getEmail());
            if (dbAccount == null) {
                dbAccount = dao.insert("", account);
                dbAccount = addAccountData(account, dbAccount);
            }
            return dbAccount;
        });
    }

    public Account addAccountData(Account account, Account dbAccount) {
        if (!account.getPhones().isEmpty()) {
            for (Phone phone : account.getPhones()) {
                dbAccount.getPhones().add(new Phone(dbAccount, phone.getNumber(), phone.getPhoneType()));
            }
            dbAccount = dao.insert("", dbAccount.getPhones());
        }
        if (!account.getAddresses().isEmpty()) {
            for (Address address : account.getAddresses()) {
                dbAccount.getAddresses().add(
                        new Address(dbAccount, address.getAddress(), address.getAddrType()));
            }
            dbAccount = dao.insert("", dbAccount.getAddresses());
        }
        if (!account.getMessengers().isEmpty()) {
            for (Messenger messenger : account.getMessengers()) {
                dbAccount.getMessengers().add(
                        new Messenger(dbAccount, messenger.getUsername(), messenger.getMsgrType()));
            }
            dbAccount = dao.insert("", dbAccount.getMessengers());
        }
        return dbAccount;
    }

    public Account editAccount(Account account, String field, Object value) {
        return transactionManager.executeTransaction(() -> {
            Account dbAccount = dao.select("", EMAIL, account.getEmail());
            if (dbAccount != null) {
                dbAccount = dao.update("", field, value, account);
            }
            return dbAccount;
        });
    }

    public Account editAccount(Account account) {
        return transactionManager.executeTransaction(() -> {
            Account dbAccount = dao.select("", EMAIL, account.getEmail());
            if (dbAccount != null) {
                dbAccount = dao.update(account);
            }
            return dbAccount;
        });
    }

    public Account deleteAccount(Account account) {
        return transactionManager.executeTransaction(() -> {
            Account dbAccount = dao.select("", EMAIL, account.getEmail());
            if (dbAccount != null) {
                dbAccount = dao.delete("", account);
            }
            return dbAccount;
        });
    }

    public Account addFriend(Account account, Account friend) {
        return transactionManager.executeTransaction(() -> {
            Account dbAccount = dao.select("", EMAIL, account.getEmail());
            if (dbAccount == null) {
                throw new IllegalArgumentException("Parameter 'account' is empty");
            }
            Account dbFriend = dao.select("", EMAIL, friend.getEmail());
            if (dbFriend == null) {
                throw new IllegalArgumentException("Parameter 'friend' is empty");
            }
            List<Friend> friends = account.getFriends();
            friends.add(new Friend(account, dbFriend));
            dbAccount = dao.insert("", friends);
            return dbAccount;
        });
    }

    public Account deleteFriend(Account account, Account friend) {
        return transactionManager.executeTransaction(() -> {
            Account dbAccount = dao.select("", EMAIL, account.getEmail());
            if (dbAccount == null) {
                throw new IllegalArgumentException("Parameter 'account' is empty");
            }
            Account dbFriend = dao.select("", EMAIL, friend.getEmail());
            if (dbFriend == null) {
                throw new IllegalArgumentException("Parameter 'friend' is empty");
            }
            List<Friend> friends = account.getFriends();
            friends.clear();
            friends.add(new Friend(account, dbFriend));
            dbAccount = dao.delete("", friends);
            return dbAccount;
        });
    }

    public List<Friend> getFriends(Account account) {
        return transactionManager.executeAction(() -> dao.select("", EMAIL, account.getEmail()).getFriends());
    }

    public boolean passwordIsValid(String password, Account account) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.matches(password, account.getPasswordHash());
    }

    public boolean changePassword(String oldPassword, String newPassword, Account account) {
        return transactionManager.executeTransaction(() -> {
            boolean passwordChanged;
            Account dbAccount = dao.select("", EMAIL, account.getEmail());
            if (dbAccount == null) {
                return false;
            }
            if (StringUtils.isEmpty(account.getPasswordHash()) || passwordIsValid(oldPassword, account)) {
                account.setPasswordHash(account.hashPassword(newPassword));
                dbAccount = dao.update("", "passwordHash", account.getPasswordHash(), account);
                passwordChanged = Objects.equals(dbAccount.getPasswordHash(), account.getPasswordHash());
            } else {
                throw new DaoRuntimeException("Old password is not valid!");
            }
            return passwordChanged;
        });
    }

}
