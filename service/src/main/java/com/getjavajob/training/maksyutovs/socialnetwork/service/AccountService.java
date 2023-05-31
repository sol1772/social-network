package com.getjavajob.training.maksyutovs.socialnetwork.service;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.AccountDao;
import com.getjavajob.training.maksyutovs.socialnetwork.dao.DaoException;
import com.getjavajob.training.maksyutovs.socialnetwork.dao.DaoRuntimeException;
import com.getjavajob.training.maksyutovs.socialnetwork.dao.DataSourceHolder;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountService {

    private static final Logger LOGGER = Logger.getLogger(AccountService.class.getName());
    private static final String EMAIL = "email";
    private AccountDao dao;
    private DataSourceHolder dataSourceHolder;

    public AccountService() {
    }

    public AccountService(AccountDao dao) {
        this.dao = dao;
        this.dataSourceHolder = dao.getDataSourceHolder();
    }

    public AccountDao getDao() {
        return dao;
    }

    public void setDao(AccountDao dao) {
        this.dao = dao;
    }


    void rollbackTransaction(Connection con) {
        if (con != null) {
            try {
                con.rollback();
                LOGGER.log(Level.WARNING, "Transaction is being rolled back");
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage());
            }
        }
    }

    public Account getAccountByEmail(String email) {
        Account dbAccount = null;
        try (Connection ignored = dataSourceHolder.getConnection()) {
            dbAccount = dao.select("", EMAIL, email);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
        return dbAccount;
    }

    public Account getAccountById(int id) {
        Account dbAccount = null;
        try (Connection ignored = dataSourceHolder.getConnection()) {
            dbAccount = dao.select("", "id", id);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
        return dbAccount;
    }

    public List<Account> getAccounts() {
        List<Account> accounts = Collections.emptyList();
        try (Connection ignored = dataSourceHolder.getConnection()) {
            accounts = dao.selectAll("");
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
        return accounts;
    }

    public List<Account> getAccountsByString(String substring, int start, int total) {
        List<Account> accounts = Collections.emptyList();
        try (Connection ignored = dataSourceHolder.getConnection()) {
            accounts = dao.selectByString(substring, start, total);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
        return accounts;
    }

    public int getAccountsCountByString(String substring, int start, int total) {
        int rows = 0;
        try (Connection ignored = dataSourceHolder.getConnection()) {
            rows = dao.selectCountByString(substring, start, total);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
        return rows;
    }

    public Account registerAccount(Account account) {
        Account dbAccount = null;
        try (Connection connection = dataSourceHolder.getConnection()) {
            boolean initialAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                dbAccount = dao.select("", EMAIL, account.getEmail());
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
                connection.commit();
            } catch (DaoException | DaoRuntimeException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
                rollbackTransaction(connection);
            } finally {
                connection.setAutoCommit(initialAutoCommit);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
        return dbAccount;
    }

    public Optional<Account> editAccount(Account account, String field, Object value) {
        Account dbAccount = null;
        try (Connection connection = dataSourceHolder.getConnection()) {
            boolean initialAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                dbAccount = dao.select("", EMAIL, account.getEmail());
                if (dbAccount != null) {
                    dbAccount = dao.update("", field, value, account);
                    connection.commit();
                }
            } catch (DaoException | DaoRuntimeException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
                rollbackTransaction(connection);
            } finally {
                connection.setAutoCommit(initialAutoCommit);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
        return Optional.ofNullable(dbAccount);
    }

    public Optional<Account> editAccount(Account account) {
        Account dbAccount = null;
        try (Connection connection = dataSourceHolder.getConnection()) {
            boolean initialAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                dbAccount = dao.select("", EMAIL, account.getEmail());
                if (dbAccount != null) {
                    dbAccount = dao.update(account);
                    connection.commit();
                }
            } catch (DaoException | DaoRuntimeException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
                rollbackTransaction(connection);
            } finally {
                connection.setAutoCommit(initialAutoCommit);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
        return Optional.ofNullable(dbAccount);
    }

    public Optional<Account> deleteAccount(Account account) {
        Account dbAccount = null;
        try (Connection connection = dataSourceHolder.getConnection()) {
            boolean initialAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                dbAccount = dao.select("", EMAIL, account.getEmail());
                if (dbAccount != null) {
                    dao.delete("", account);
                    connection.commit();
                }
            } catch (DaoException | DaoRuntimeException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
                rollbackTransaction(connection);
            } finally {
                connection.setAutoCommit(initialAutoCommit);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
        return Optional.ofNullable(dbAccount);
    }

    public Optional<Account> addFriend(Account account, Account friend) {
        Account dbAccount = null;
        try (Connection connection = dataSourceHolder.getConnection()) {
            boolean initialAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                dbAccount = dao.select("", EMAIL, account.getEmail());
                if (dbAccount == null) {
                    return Optional.empty();
                }
                Account dbFriend = dao.select("", EMAIL, friend.getEmail());
                if (dbFriend == null) {
                    return Optional.of(dbAccount);
                }
                List<Friend> friends = account.getFriends();
                friends.add(new Friend(account, dbFriend));
                dbAccount = dao.insert("", friends);
                connection.commit();
            } catch (DaoException | DaoRuntimeException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
                rollbackTransaction(connection);
            } finally {
                connection.setAutoCommit(initialAutoCommit);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
        return Optional.ofNullable(dbAccount);
    }

    public Optional<Account> deleteFriend(Account account, Account friend) {
        Account dbAccount = null;
        try (Connection connection = dataSourceHolder.getConnection()) {
            boolean initialAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                dbAccount = dao.select("", EMAIL, account.getEmail());
                if (dbAccount == null) {
                    return Optional.empty();
                }
                Account dbFriend = dao.select("", EMAIL, friend.getEmail());
                if (dbFriend == null) {
                    return Optional.of(dbAccount);
                }
                List<Friend> friends = account.getFriends();
                friends.clear();
                friends.add(new Friend(account, dbFriend));
                dbAccount = dao.delete("", friends);
                connection.commit();
            } catch (DaoException | DaoRuntimeException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
                rollbackTransaction(connection);
            } finally {
                connection.setAutoCommit(initialAutoCommit);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
        return Optional.ofNullable(dbAccount);
    }

    public List<Friend> getFriends(Account account) {
        List<Friend> friends = Collections.emptyList();
        try (Connection ignored = dataSourceHolder.getConnection()) {
            friends = dao.select("", EMAIL, account.getEmail()).getFriends();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
        return friends;
    }

    public boolean passwordIsValid(String password, Account account) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.matches(password, account.getPasswordHash());
    }

    public boolean changePassword(String oldPassword, String newPassword, Account account) {
        boolean passwordChanged = false;
        try (Connection connection = dataSourceHolder.getConnection()) {
            boolean initialAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                Account dbAccount = dao.select("", EMAIL, account.getEmail());
                if (dbAccount == null) {
                    return false;
                }
                if (StringUtils.isEmpty(account.getPasswordHash()) || passwordIsValid(oldPassword, account)) {
                    account.setPasswordHash(account.hashPassword(newPassword));
                    dbAccount = dao.update("", "passwordHash", account.getPasswordHash(), account);
                    passwordChanged = Objects.equals(dbAccount.getPasswordHash(), account.getPasswordHash());
                    connection.commit();
                } else {
                    LOGGER.log(Level.WARNING, "Old password is not valid!");
                }
            } catch (DaoException | DaoRuntimeException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
                rollbackTransaction(connection);
            } finally {
                connection.setAutoCommit(initialAutoCommit);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            dataSourceHolder.returnConnection();
        }
        return passwordChanged;
    }

}
