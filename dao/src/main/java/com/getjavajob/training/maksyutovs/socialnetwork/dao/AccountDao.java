package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.*;

import java.io.InputStream;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountDao implements CrudDao<Account, Object> {

    static final Logger LOGGER = Logger.getLogger(AccountDao.class.getName());
    private static final String CREATE = "INSERT INTO ";
    private static final String READ = "SELECT * FROM ";
    private static final String UPDATE = "UPDATE ";
    private static final String DELETE = "DELETE FROM ";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String USERNAME = "username";
    private static final String BIRTHDATE = "dateOfBirth";
    private static final String EMAIL = "email";
    public final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    public final DateTimeFormatter formatterT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private ConnectionPool pool;
    private Connection connection;

    public AccountDao() {
    }

    public AccountDao(String resourceName) {
        this.pool = ConnectionPool.getInstance(resourceName);
    }

    public AccountDao(ConnectionPool pool) {
        this.pool = pool;
    }

    public AccountDao(Connection connection) {
        this.connection = connection;
        this.pool = ConnectionPool.getInstance("");
    }

    public ConnectionPool getPool() {
        return pool;
    }

    public void setPool(ConnectionPool pool) {
        this.pool = pool;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
            }
        }
    }

    Account createAccountFromResult(ResultSet rs) throws SQLException, ParseException {
        Account account = new Account(rs.getString(FIRST_NAME),
                rs.getString(LAST_NAME),
                rs.getString(USERNAME),
                formatter.parse(rs.getString(BIRTHDATE)),
                rs.getString(EMAIL));
        account.setId(rs.getInt("id"));
        account.setPasswordHash(rs.getString("passwordHash"));
        account.setMiddleName(rs.getString("middleName"));
        account.setGender(rs.getString("gender") == null ?
                Gender.M : Gender.valueOf(rs.getString("gender")));
        account.setAddInfo(rs.getString("addInfo"));
        account.setRegisteredAt(rs.getString("registeredAt") == null ? new Date(0) :
                formatter.parse(rs.getString("registeredAt")));
        account.setImage(rs.getBytes("image"));
        return account;
    }

    Account getAccountData(Account account) {
        connection = pool.getConnection();
        // contacts, friends
        String queryPhone = "SELECT * FROM Phone WHERE accId=?;";
        String queryAddress = "SELECT * FROM Address WHERE accId=?;";
        String queryMessenger = "SELECT * FROM Messenger WHERE accId=?;";
        String queryFriend = "SELECT * FROM Account a INNER JOIN Friend f " +
                "ON a.id = f.friendId AND f.accId=?;";
        String queryMessage = "SELECT * FROM Message m INNER JOIN Account a " +
                "ON m.trgtId = a.id WHERE accId=?;";
        ResultSet rs = null;
        try (PreparedStatement pstPhone = connection.prepareStatement(queryPhone);
             PreparedStatement pstAddress = connection.prepareStatement(queryAddress);
             PreparedStatement pstMessenger = connection.prepareStatement(queryMessenger);
             PreparedStatement pstFriend = connection.prepareStatement(queryFriend);
             PreparedStatement pstMessage = connection.prepareStatement(queryMessage)) {
            pstPhone.setInt(1, account.getId());
            rs = pstPhone.executeQuery();
            List<Phone> phones = account.getPhones();
            while (rs.next()) {
                phones.add(new Phone(account, rs.getString("phoneNmr"),
                        PhoneType.valueOf(rs.getString("phoneType"))));
            }

            pstAddress.setInt(1, account.getId());
            rs = pstAddress.executeQuery();
            List<Address> addresses = account.getAddresses();
            while (rs.next()) {
                addresses.add(new Address(account, rs.getString("addr"),
                        AddressType.valueOf(rs.getString("addrType"))));
            }

            pstMessenger.setInt(1, account.getId());
            rs = pstMessenger.executeQuery();
            List<Messenger> messengers = account.getMessengers();
            while (rs.next()) {
                messengers.add(new Messenger(account, rs.getString(USERNAME),
                        MessengerType.valueOf(rs.getString("msngrType"))));
            }

            pstFriend.setInt(1, account.getId());
            rs = pstFriend.executeQuery();
            List<Friend> friends = account.getFriends();
            while (rs.next()) {
                Account friendAccount = new Account(rs.getString(FIRST_NAME),
                        rs.getString(LAST_NAME),
                        rs.getString(USERNAME),
                        formatter.parse(rs.getString(BIRTHDATE)),
                        rs.getString(EMAIL));
                friendAccount.setId(rs.getInt("id"));
                friends.add(new Friend(account, friendAccount));
            }

            pstMessage.setInt(1, account.getId());
            rs = pstMessage.executeQuery();
            List<Message> messages = account.getMessages();
            while (rs.next()) {
                Account targetAccount = new Account(rs.getString(FIRST_NAME),
                        rs.getString(LAST_NAME),
                        rs.getString(USERNAME),
                        formatter.parse(rs.getString(BIRTHDATE)),
                        rs.getString(EMAIL));
                Message message = new Message(account, targetAccount,
                        MessageType.valueOf(rs.getString("msgType")), rs.getString("txtContent"));
                message.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                messages.add(message);
            }
        } catch (SQLException | ParseException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            closeResultSet(rs);
            connection = null;
        }
        return account;
    }

    @Override
    public Account insert(String query, Account account) {
        connection = pool.getConnection();
        if (query.isEmpty()) {
            query = "Account(firstName,middleName,lastName,username,email," +
                    "dateOfBirth,gender,addInfo,passwordHash,registeredAt,image)" +
                    " VALUES (?,?,?,?,?,?,?,?,?,now(),?);";
        }
        String queryInsert = CREATE + query;
        try (PreparedStatement pst = connection.prepareStatement(queryInsert)) {
            if (queryInsert.contains("?")) {
                pst.setString(1, account.getFirstName());
                pst.setString(2, account.getMiddleName());
                pst.setString(3, account.getLastName());
                pst.setString(4, account.getUserName());
                pst.setString(5, account.getEmail());
                pst.setString(6, formatter.format(account.getDateOfBirth()));
                pst.setString(7, String.valueOf(account.getGender() != null ?
                        account.getGender().toString().charAt(0) : 'M'));
                pst.setString(8, account.getAddInfo());
                pst.setString(9, account.getPasswordHash());
                pst.setBytes(10, account.getImage());
            }
            pst.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            connection = null;
        }
        return select("", EMAIL, account.getEmail());
    }

    public <T> Account insert(String query, List<T> accountData) {
        connection = pool.getConnection();
        Account account = null;
        if (accountData.isEmpty()) {
            return null;
        }

        T contact = accountData.get(0);
        if (query.isEmpty()) {
            if (contact instanceof Phone) {
                query = "Phone(accId,phoneNmr,phoneType) VALUES (?,?,?);";
                account = ((Phone) contact).getAccount();
            } else if (contact instanceof Address) {
                query = "Address(accId,addr,addrType) VALUES (?,?,?);";
                account = ((Address) contact).getAccount();
            } else if (contact instanceof Messenger) {
                query = "Messenger(accId,username,msngrType) VALUES (?,?,?);";
                account = ((Messenger) contact).getAccount();
            } else if (contact instanceof Friend) {
                query = "Friend(accId,friendId) VALUES (?,?), (?,?);";
                account = ((Friend) contact).getAccount();
            } else if (contact instanceof Message) {
                query = "Message(accId,trgtId,txtContent,mediaContent,msgType,createdAt) VALUES (?,?,?,?,?,now());";
                account = ((Message) contact).getAccount();
            }
        }
        assert account != null;

        String queryInsert = CREATE + query;
        try (PreparedStatement pst = connection.prepareStatement(queryInsert)) {
            for (T data : accountData) {
                if (queryInsert.contains("?")) {
                    pst.setInt(1, account.getId());
                    if (data instanceof Phone) {
                        pst.setString(2, ((Phone) data).getNumber());
                        pst.setString(3, ((Phone) data).getPhoneType().toString());
                    } else if (data instanceof Address) {
                        pst.setString(2, ((Address) data).getAddress());
                        pst.setString(3, ((Address) data).getAddrType().toString());
                    } else if (data instanceof Messenger) {
                        pst.setString(2, ((Messenger) data).getUsername());
                        pst.setString(3, ((Messenger) data).getMsngrType().toString());
                    } else if (data instanceof Friend) {
                        pst.setInt(2, ((Friend) data).getFriendId());
                        pst.setInt(3, ((Friend) data).getFriendId());
                        pst.setInt(4, account.getId());
                    } else if (data instanceof Message) {
                        pst.setInt(2, ((Message) data).getTrgtId());
                        pst.setString(3, ((Message) data).getTextContent());
                        pst.setBytes(4, ((Message) data).getMediaContent());
                        pst.setString(5, ((Message) data).getMsgType().toString());
                    }
                }
                pst.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            connection = null;
        }
        return select("", EMAIL, account.getEmail());
    }

    @Override
    public Account select(String query, String field, Object value) {
        connection = pool.getConnection();
        Account account = null;
        ResultSet rs = null;
        if (query.isEmpty()) {
            query = "Account WHERE " + field + "=?;";
        }
        String querySelect = READ + query;
        try (PreparedStatement pst = connection.prepareStatement(querySelect)) {
            if (querySelect.contains("?")) {
                if (value instanceof String) {
                    pst.setString(1, (String) value);
                } else if (value instanceof Integer) {
                    pst.setInt(1, (int) value);
                }
            }
            rs = pst.executeQuery();
            if (rs.next()) {
                account = getAccountData(createAccountFromResult(rs));
            }
        } catch (SQLException | ParseException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            closeResultSet(rs);
            connection = null;
        }
        return account;
    }

    public List<Account> selectAll(String query) {
        connection = pool.getConnection();
        List<Account> accounts = new ArrayList<>();
        if (query.isEmpty()) {
            query = "Account";
        }
        String querySelect = READ + query;
        ResultSet rs = null;
        try (PreparedStatement pst = connection.prepareStatement(querySelect)) {
            rs = pst.executeQuery();
            while (rs.next()) {
                Account account = getAccountData(createAccountFromResult(rs));
                accounts.add(account);
            }
        } catch (SQLException | ParseException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            closeResultSet(rs);
            connection = null;
        }
        return accounts;
    }

    public List<Account> selectByString(String substring, int start, int total) {
        connection = pool.getConnection();
        List<Account> accounts = new ArrayList<>();
        String searchString = "%" + substring + "%";
        String querySelect = READ + "Account WHERE firstName LIKE ? OR lastName LIKE ? ORDER BY lastName" +
                (total > 0 ? " LIMIT " + (start - 1) + "," + total : "");
        ResultSet rs = null;
        try (PreparedStatement pst = connection.prepareStatement(querySelect)) {
            pst.setString(1, searchString);
            pst.setString(2, searchString);
            rs = pst.executeQuery();
            while (rs.next()) {
                Account account = getAccountData(createAccountFromResult(rs));
                accounts.add(account);
            }
        } catch (SQLException | ParseException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            closeResultSet(rs);
            connection = null;
        }
        return accounts;
    }

    @Override
    public Account update(String query, String field, Object value, Account account) {
        connection = pool.getConnection();
        if (query.isEmpty()) {
            query = "Account SET " + field + "=? WHERE email=?;";
        }
        String queryUpdate = UPDATE + query;
        try (PreparedStatement pst = connection.prepareStatement(queryUpdate)) {
            if (queryUpdate.contains("?")) {
                if (value instanceof String) {
                    pst.setString(1, (String) value);
                } else if (value instanceof Integer) {
                    pst.setInt(1, (int) value);
                } else if (value instanceof InputStream) {
                    pst.setBinaryStream(1, (InputStream) value);
                }
                pst.setString(2, account.getEmail());
            }
            pst.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            connection = null;
        }
        return select("", EMAIL, account.getEmail());
    }

    public Account update(Account account) {
        connection = pool.getConnection();
        String query = "Account SET firstName=?,middleName=?,lastName=?,username=?,dateOfBirth=?,gender=?,addInfo=?" +
                " WHERE email=?;";
        String queryUpdate = UPDATE + query;
        try (PreparedStatement pst = connection.prepareStatement(queryUpdate)) {
            pst.setString(1, account.getFirstName());
            pst.setString(2, account.getMiddleName());
            pst.setString(3, account.getLastName());
            pst.setString(4, account.getUserName());
            pst.setString(5, formatter.format(account.getDateOfBirth()));
            pst.setString(6, String.valueOf(account.getGender() != null ?
                    account.getGender().toString().charAt(0) : 'M'));
            pst.setString(7, account.getAddInfo());
            pst.setString(8, account.getEmail());
            pst.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            connection = null;
        }
        return select("", EMAIL, account.getEmail());
    }

    @Override
    public Account delete(String query, Account account) {
        connection = pool.getConnection();
        List<String> queries = new ArrayList<>();
        if (query.isEmpty()) {
            queries.add("Account WHERE email=?;");
            queries.add("Phone WHERE accId=?;");
            queries.add("Address WHERE accId=?;");
            queries.add("Messenger WHERE accId=?;");
            queries.add("Friend WHERE accId=? OR friendId=?;");
            queries.add("Message WHERE accId=? OR trgtId=?");
        } else {
            queries.add(query);
        }
        for (String queryText : queries) {
            String queryDelete = DELETE + queryText;
            try (PreparedStatement pst = connection.prepareStatement(queryDelete)) {
                if (queryDelete.contains("?")) {
                    int accountId = account.getId();
                    if (queryDelete.contains("Account")) {
                        pst.setString(1, account.getEmail());
                    } else {
                        pst.setInt(1, accountId);
                        if (queryDelete.contains("Friend") || queryDelete.contains("Message")) {
                            pst.setInt(2, accountId);
                        }
                    }
                }
                pst.executeUpdate();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
            }
        }
        connection = null;
        return select("", "Email", account.getEmail());
    }

    public <T> Account delete(String query, List<T> accountData) {
        connection = pool.getConnection();
        Account account = null;
        if (accountData.isEmpty()) {
            return null;
        }
        T contact = accountData.get(0);
        if (query.isEmpty()) {
            if (contact instanceof Phone) {
                query = "Phone WHERE accId=? AND phoneNmr=? AND phoneType=?;";
                account = ((Phone) contact).getAccount();
            } else if (contact instanceof Address) {
                query = "Address WHERE accId=? AND addr=? AND addrType=?;";
                account = ((Address) contact).getAccount();
            } else if (contact instanceof Messenger) {
                query = "Messenger WHERE accId=? AND username=? AND msngrType=?;";
                account = ((Messenger) contact).getAccount();
            } else if (contact instanceof Friend) {
                query = "Friend WHERE accId=? AND friendId=?;";
                account = ((Friend) contact).getAccount();
            } else if (contact instanceof Message) {
                query = "Message WHERE accId=? AND trgtId=? AND createdAt=?;";
                account = ((Message) contact).getAccount();
            }
        }

        assert account != null;
        int accountId = account.getId();
        String queryDelete = DELETE + query;
        try (PreparedStatement pst = connection.prepareStatement(queryDelete)) {
            for (T data : accountData) {
                if (queryDelete.contains("?")) {
                    pst.setInt(1, accountId);
                    if (data instanceof Phone) {
                        pst.setString(2, ((Phone) data).getNumber());
                        pst.setString(3, ((Phone) data).getPhoneType().toString());
                    } else if (data instanceof Address) {
                        pst.setString(2, ((Address) data).getAddress());
                        pst.setString(3, ((Address) data).getAddrType().toString());
                    } else if (data instanceof Messenger) {
                        pst.setString(2, ((Messenger) data).getUsername());
                        pst.setString(3, ((Messenger) data).getMsngrType().toString());
                    } else if (data instanceof Friend) {
                        pst.setInt(2, ((Friend) data).getFriendId());
                    } else if (data instanceof Message) {
                        pst.setInt(2, ((Message) data).getTrgtId());
                        pst.setString(3, ((Message) data).getCreatedAt().toString());
                    }
                }
                pst.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        } finally {
            connection = null;
        }
        return select("", EMAIL, account.getEmail());
    }

}