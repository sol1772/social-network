package com.getjavajob.training.maksyutovs.socialnetwork.service;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.AccountDao;
import com.getjavajob.training.maksyutovs.socialnetwork.dao.FriendDao;
import com.getjavajob.training.maksyutovs.socialnetwork.dao.MessageDao;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Friend;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Message;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.MessageType;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.dto.AccountDto;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.dto.Mapper;
import com.getjavajob.training.maksyutovs.socialnetwork.repositories.AccountRepository;
import com.getjavajob.training.maksyutovs.socialnetwork.service.validation.ValidationRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
    private final AccountRepository accountRepository;
    private final AccountDao accountDao;
    private final FriendDao friendDao;
    private final MessageDao messageDao;
    private final Mapper mapper = new Mapper();

    @Autowired
    public AccountService(AccountRepository accountRepository,
                          AccountDao accountDao, FriendDao friendDao, MessageDao messageDao) {
        this.accountRepository = accountRepository;
        this.accountDao = accountDao;
        this.friendDao = friendDao;
        this.messageDao = messageDao;
    }

    public Account getAccountById(int id) {
        return accountDao.select(id);
    }

    public Account getFullAccountById(int id) {
        return accountRepository.findAccountByIdEagerly(id);
    }

    public Account getAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public Account getFullAccountByEmail(String email) {
        return accountRepository.findAccountByEmailEagerly(email);
    }

    public Page<Account> getPageAccountsByString(String substring, int page, int itemsPerPage) {
        String searchString = "%" + substring + "%";
        return accountRepository.findByFirstNameLikeOrLastNameLike(searchString, searchString,
                PageRequest.of(page, itemsPerPage, Sort.by("lastName")));
    }

    public List<AccountDto> getAccountsByString(String substring, int page, int itemsPerPage) {
        String searchString = "%" + substring + "%";
        List<Account> accounts = accountRepository.findByFirstNameLikeOrLastNameLike(searchString, searchString,
                PageRequest.of(page, itemsPerPage, Sort.by("lastName"))).getContent();
        return accounts.stream().map(mapper::toAccountDto).collect(Collectors.toList());
    }

    public Account validateAccount(Account account) {
        Account dbAccount = accountRepository.findByEmail(account.getEmail());
        if (dbAccount == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("Error when validating an account {}", account);
            }
            throw new ValidationRuntimeException("Account with email '" + account.getEmail() + "' does not exist");
        }
        return dbAccount;
    }

    public boolean checkAccount(Account account) {
        if (accountRepository.findByEmail(account.getEmail()) == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("Error when validating an account {}", account);
            }
            throw new ValidationRuntimeException("Account with email '" + account.getEmail() + "' does not exist");
        }
        return true;
    }

    public boolean accountExists(int id) {
        return accountRepository.existsById(id);
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
        boolean deleted = messageDao.delete(id);
        if (deleted) {
            if (logger.isInfoEnabled()) {
                logger.info("Deleted message with id {}", id);
            }
            return true;
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("Error when deleting the message with id {}", id);
            }
            return false;
        }
    }

    @Transactional
    public Account registerAccount(Account account) {
        Account dbAccount = accountRepository.save(account);
        if (logger.isInfoEnabled()) {
            logger.info("Registered account {}", dbAccount);
        }
        return dbAccount;
    }

    @Transactional
    public Account editAccount(Account account) {
        Account dbAccount = accountRepository.save(account);
        if (logger.isInfoEnabled()) {
            logger.info("Updated account {}", dbAccount);
        }
        return dbAccount;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public boolean deleteAccount(int id) {
        try {
            accountRepository.deleteById(id);
            if (logger.isInfoEnabled()) {
                logger.info("Deleted account with id {}", id);
            }
            return true;
        } catch (EmptyResultDataAccessException e) {
            if (logger.isErrorEnabled()) {
                logger.error("Error when deleting the account with id {}", id);
            }
            return false;
        }
    }

    @Transactional
    public Friend addFriend(Account account, Account friendAccount) {
        Account dbAccount = validateAccount(account);
        Account dbFriend = validateAccount(friendAccount);
        Friend friend = friendDao.insert(new Friend(dbAccount, dbFriend));
        if (logger.isInfoEnabled()) {
            logger.info("Added friend {}", friend);
        }
        return friend;
    }

    @Transactional
    public boolean deleteFriend(Account account, Account friendAccount) {
        Account dbAccount = validateAccount(account);
        Account dbFriend = validateAccount(friendAccount);
        Friend friend = friendDao.selectFriend(dbAccount, dbFriend);
        if (friend != null) {
            accountDao.delete(friend);
            if (logger.isInfoEnabled()) {
                logger.info("Deleted friend {}", friend);
            }
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
                Account dbAccount = accountRepository.save(account);
                passwordChanged = Objects.equals(dbAccount.getPasswordHash(), account.getPasswordHash());
                if (logger.isInfoEnabled()) {
                    logger.info("Password changed for account {}", dbAccount);
                }
            } else {
                if (logger.isWarnEnabled()) {
                    logger.warn("Password change error for account {} (old password is not valid)", account);
                }
                throw new ValidationRuntimeException("Old password is not valid!");
            }
        }
        return passwordChanged;
    }

}
