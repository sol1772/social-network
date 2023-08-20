package com.getjavajob.training.maksyutovs.socialnetwork.service;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.AccountDao;
import com.getjavajob.training.maksyutovs.socialnetwork.dao.DaoRuntimeException;
import com.getjavajob.training.maksyutovs.socialnetwork.dao.FriendDao;
import com.getjavajob.training.maksyutovs.socialnetwork.dao.MessageDao;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Friend;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Message;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.MessageType;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.dto.AccountDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class AccountService {

    private AccountDao accountDao;
    private FriendDao friendDao;
    private MessageDao messageDao;

    public AccountService() {
    }

    @Autowired
    public AccountService(AccountDao accountDao, FriendDao friendDao, MessageDao messageDao) {
        this.accountDao = accountDao;
        this.friendDao = friendDao;
        this.messageDao = messageDao;
    }

    public AccountDao getDao() {
        return accountDao;
    }

    public void setDao(AccountDao dao) {
        this.accountDao = dao;
    }

    public Account getAccountById(int id) {
        return accountDao.select(id);
    }

    public Account getFullAccountById(int id) {
        return accountDao.selectById(id);
    }

    public Account getAccountByEmail(String email) {
        return accountDao.selectByEmail(email);
    }

    public List<AccountDto> getAccountsByString(String substring, int start, int total) {
        return accountDao.selectByString(substring, start, total);
    }

    public int getAccountsCountByString(String substring) {
        return accountDao.selectCountByString(substring);
    }

    public Account validateAccount(Account account) {
        Account dbAccount = accountDao.selectByEmail(account.getEmail());
        if (dbAccount == null) {
            throw new ValidationRuntimeException("Account with email '" + account.getEmail() + "' does not exist");
        }
        return dbAccount;
    }

    public boolean checkAccount(Account account) {
        if (!accountDao.checkByEmail(account.getEmail())) {
            throw new ValidationRuntimeException("Account with email '" + account.getEmail() + "' does not exist");
        }
        return true;
    }

    public boolean accountExists(String email) {
        return accountDao.checkByEmail(email);
    }

    public List<Account> getTargetAccounts(Account account, MessageType type) {
        return accountDao.selectTargetAccounts(account, type);
    }

    public List<Message> getMessages(Account account, Account targetAccount, MessageType type) {
        return messageDao.selectMessages(account, targetAccount, type);
    }

    @Transactional
    public Message sendMessage(Message message) {
        return messageDao.insert(message);
    }

    @Transactional
    public boolean deleteMessage(int id) {
        return messageDao.delete(id);
    }

    @Transactional
    public Account registerAccount(Account account) {
        Account dbAccount;
        try {
            dbAccount = accountDao.insert(account);
        } catch (DaoRuntimeException e) {
            throw new DaoRuntimeException(e.getMessage(), e);
        }
        return dbAccount;
    }

    @Transactional
    public Account editAccount(Account account) {
        return accountDao.update(account);
    }

    @Transactional
    public boolean deleteAccount(int id) {
        if (accountDao.select(id) != null) {
            return accountDao.delete(id);
        }
        return false;
    }

    @Transactional
    public Friend addFriend(Account account, Account friendAccount) {
        Account dbAccount = validateAccount(account);
        Account dbFriend = validateAccount(friendAccount);
        Friend friend = new Friend(dbAccount, dbFriend);
        return friendDao.insert(friend);
    }

    @Transactional
    public boolean deleteFriend(Account account, Account friendAccount) {
        Account dbAccount = validateAccount(account);
        Account dbFriend = validateAccount(friendAccount);
        Friend friend = friendDao.selectFriend(dbAccount, dbFriend);
        if (friend != null) {
            accountDao.delete(friend);
            return true;
        } else {
            return false;
        }
    }

    public boolean passwordIsValid(String password, Account account) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return StringUtils.isEmpty(account.getPasswordHash()) ||
                bCryptPasswordEncoder.matches(password, account.getPasswordHash());
    }

    @Transactional
    public boolean changePassword(String oldPassword, String newPassword, Account account) {
        boolean passwordChanged = false;
        if (checkAccount(account)) {
            if (passwordIsValid(oldPassword, account)) {
                account.setPasswordHash(account.hashPassword(newPassword));
                Account dbAccount = accountDao.update(account);
                passwordChanged = Objects.equals(dbAccount.getPasswordHash(), account.getPasswordHash());
            } else {
                throw new DaoRuntimeException("Old password is not valid!");
            }
        }
        return passwordChanged;
    }

}
