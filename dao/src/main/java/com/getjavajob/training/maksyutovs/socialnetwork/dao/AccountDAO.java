package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO implements CrudDAO<Account, Object> {

    private static final String CREATE = "INSERT INTO ";
    private static final String READ = "SELECT * FROM ";
    private static final String UPDATE = "UPDATE ";
    private static final String DELETE = "DELETE FROM ";
    public final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private Connection connection;

    public AccountDAO() {
    }

    public AccountDAO(Connection connection) {
        this.connection = connection;
    }

    public AccountDAO(String resourceName) {
        ConnectionData conData = new ConnectionData();
        conData.connect(resourceName);
        connection = conData.getConnection();
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    private Account clone(Account account) {
        Account dbAccount = new Account(account.getFirstName(), account.getLastName(), account.getUserName(),
                account.getDateOfBirth(), account.getEmail());
        dbAccount.setId(account.getId());
        dbAccount.setPasswordHash(account.getPasswordHash());
        dbAccount.setMiddleName(account.getMiddleName());
        dbAccount.setGender(account.getGender());
        dbAccount.setAddInfo(account.getAddInfo());
        dbAccount.setRegisteredAt(account.getRegisteredAt());

        for (Account.Phone phone : account.getPhones()) {
            dbAccount.getPhones().add(dbAccount.new Phone(phone.getNumber(), phone.getPhoneType()));
        }
        for (Account.Address address : account.getAddresses()) {
            dbAccount.getAddresses().add(dbAccount.new Address(address.getAddress(), address.getAddrType()));
        }
        for (Account.Messenger messenger : account.getMessengers()) {
            dbAccount.getMessengers().add(dbAccount.new Messenger(messenger.getUsername(), messenger.getMsngrType()));
        }
        for (Account.Friend friend : account.getFriends()) {
            dbAccount.getFriends().add(dbAccount.new Friend(friend.getAccount()));
        }

        return dbAccount;
    }

    private Account createAccountFromResult(ResultSet rs) throws SQLException, ParseException {
        Account account = new Account(rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("username"),
                formatter.parse(rs.getString("dateOfBirth")),
                rs.getString("email"));
        account.setId(rs.getInt("id"));
        account.setPasswordHash(rs.getString("passwordHash"));
        account.setMiddleName(rs.getString("middleName"));
        account.setGender(rs.getString("gender") == null ?
                Account.Gender.M : Account.Gender.valueOf(rs.getString("gender")));
        account.setAddInfo(rs.getString("addInfo"));
        account.setRegisteredAt(rs.getString("registeredAt") == null ? new Date(0) :
                formatter.parse(rs.getString("registeredAt")));

        return account;
    }

    private Account getAccountData(Account account) {
        // contacts, friends
        String queryPhone = "SELECT * FROM PHONE WHERE accId=?;";
        String queryAddress = "SELECT * FROM ADDRESS WHERE accId=?;";
        String queryMessenger = "SELECT * FROM MESSENGER WHERE accId=?;";
        String queryFriend = "SELECT * FROM ACCOUNT a INNER JOIN FRIEND f " +
                "ON a.ID = f.friendID AND f.accId=?;";
        ResultSet rs = null;
        try (PreparedStatement pstPhone = connection.prepareStatement(queryPhone);
             PreparedStatement pstAddress = connection.prepareStatement(queryAddress);
             PreparedStatement pstMessenger = connection.prepareStatement(queryMessenger);
             PreparedStatement pstFriend = connection.prepareStatement(queryFriend)) {
            pstPhone.setInt(1, account.getId());
            rs = pstPhone.executeQuery();
            List<Account.Phone> phones = account.getPhones();
            while (rs.next()) {
                phones.add(account.new Phone(rs.getString("phoneNmr"),
                        Account.PhoneType.valueOf(rs.getString("phoneType"))));
            }

            pstAddress.setInt(1, account.getId());
            rs = pstAddress.executeQuery();
            List<Account.Address> addresses = account.getAddresses();
            while (rs.next()) {
                addresses.add(account.new Address(rs.getString("addr"),
                        Account.AddressType.valueOf(rs.getString("addrType"))));
            }

            pstMessenger.setInt(1, account.getId());
            rs = pstMessenger.executeQuery();
            List<Account.Messenger> messengers = account.getMessengers();
            while (rs.next()) {
                messengers.add(account.new Messenger(rs.getString("username"),
                        Account.MessengerType.valueOf(rs.getString("msngrType"))));
            }

            pstFriend.setInt(1, account.getId());
            rs = pstFriend.executeQuery();
            List<Account.Friend> friends = account.getFriends();
            while (rs.next()) {
                Account friendAccount = new Account(rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("username"),
                        formatter.parse(rs.getString("dateOfBirth")),
                        rs.getString("email"));
                friendAccount.setId(rs.getInt("id"));
                friends.add(account.new Friend(friendAccount));
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return account;
    }

    @Override
    public Account insert(String query, Account account) {
        if (query.isEmpty()) {
            query = "ACCOUNT(firstName,middleName,lastName,username,email," +
                    "dateOfBirth,gender,addInfo,passwordHash,registeredAt)" +
                    " VALUES (?,?,?,?,?,?,?,?,?,now());";
        }
        String queryInsert = CREATE + query;
        try (PreparedStatement pst = connection.prepareStatement(queryInsert)) {
            connection.setAutoCommit(false);
            if (queryInsert.contains("?")) {
                pst.setString(1, account.getFirstName());
                pst.setString(2, account.getMiddleName());
                pst.setString(3, account.getLastName());
                pst.setString(4, account.getUserName());
                pst.setString(5, account.getEmail());
                pst.setString(6, formatter.format(account.getDateOfBirth()));
                pst.setString(7, String.valueOf(account.getGender() != null ? account.getGender().toString().charAt(0) : 'M'));
                pst.setString(8, account.getAddInfo());
                pst.setString(9, account.getPasswordHash());
            }
            pst.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return select("", "Email", account.getEmail());
    }

    public <T> Account insert(String query, List<T> accountData) {
        Account account = null;
        if (accountData.isEmpty()) {
            return null;
        }

        T contact = accountData.get(0);
        if (query.isEmpty()) {
            if (contact instanceof Account.Phone) {
                query = "PHONE(accId,phoneNmr,phoneType) VALUES (?,?,?);";
                account = ((Account.Phone) contact).getAccount();
            } else if (contact instanceof Account.Address) {
                query = "ADDRESS(accId,addr,addrType) VALUES (?,?,?);";
                account = ((Account.Address) contact).getAccount();
            } else if (contact instanceof Account.Messenger) {
                query = "MESSENGER(accId,username,msngrType) VALUES (?,?,?);";
                account = ((Account.Messenger) contact).getAccount();
            } else if (contact instanceof Account.Friend) {
                query = "FRIEND(accId,friendID) VALUES (?,?);" +
                        "INSERT INTO FRIEND(accId,friendID) VALUES (?,?);";
                account = ((Account.Friend) contact).getAccount();
            }
        }
        assert account != null;

        String queryInsert = CREATE + query;
        try (PreparedStatement pst = connection.prepareStatement(queryInsert)) {
            connection.setAutoCommit(false);
            for (T data : accountData) {
                if (queryInsert.contains("?")) {
                    pst.setInt(1, account.getId());
                    if (data instanceof Account.Phone) {
                        pst.setString(2, ((Account.Phone) data).getNumber());
                        pst.setString(3, ((Account.Phone) data).getPhoneType().toString());
                    } else if (data instanceof Account.Address) {
                        pst.setString(2, ((Account.Address) data).getAddress());
                        pst.setString(3, ((Account.Address) data).getAddrType().toString());
                    } else if (data instanceof Account.Messenger) {
                        pst.setString(2, ((Account.Messenger) data).getUsername());
                        pst.setString(3, ((Account.Messenger) data).getMsngrType().toString());
                    } else if (data instanceof Account.Friend) {
                        pst.setInt(2, ((Account.Friend) data).getFriendId());
                        pst.setInt(3, ((Account.Friend) data).getFriendId());
                        pst.setInt(4, account.getId());
                    }
                }
                pst.executeUpdate();
                connection.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return select("", "Email", account.getEmail());
    }

    @Override
    public Account select(String query, String field, Object value) {
        Account account = null;
        ResultSet rs = null;
        if (query.isEmpty()) {
            query = "ACCOUNT WHERE " + field + "=?;";
        }
        String querySelect = READ + query;
        try (PreparedStatement pst = connection.prepareStatement(querySelect)) {
            if (querySelect.contains("?")) {
                try {
                    pst.setString(1, (String) value);
                } catch (ClassCastException e) {
                    try {
                        pst.setInt(1, (int) value);
                    } catch (ClassCastException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            rs = pst.executeQuery();
            if (rs.next()) {
                account = getAccountData(createAccountFromResult(rs));
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return account;
    }

    public List<Account> selectAll(String query) {
        List<Account> accounts = new ArrayList<>();
        if (query.isEmpty()) {
            query = "ACCOUNT";
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
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return accounts;
    }

    @Override
    public Account update(String query, String field, Object value, Account account) {
        if (query.isEmpty()) {
            query = "ACCOUNT SET " + field + "=? WHERE EMAIL=?;";
        }
        String queryUpdate = UPDATE + query;
        try (PreparedStatement pst = connection.prepareStatement(queryUpdate)) {
            connection.setAutoCommit(false);
            if (queryUpdate.contains("?")) {
                try {
                    pst.setString(1, (String) value);
                    pst.setString(2, account.getEmail());
                } catch (ClassCastException e) {
                    try {
                        pst.setInt(1, (int) value);
                        pst.setString(2, account.getEmail());
                    } catch (ClassCastException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            pst.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return select("", "Email", account.getEmail());
    }

    @Override
    public Account delete(String query, Account account) {
        if (query.isEmpty()) {
            query = "ACCOUNT WHERE EMAIL=?;" +
                    "DELETE FROM PHONE WHERE accId=?;" +
                    "DELETE FROM ADDRESS WHERE accId=?;" +
                    "DELETE FROM MESSENGER WHERE accId=?;" +
                    "DELETE FROM FRIEND WHERE accId=? OR friendID=?";
        }
        String queryDelete = DELETE + query;
        try (PreparedStatement pst = connection.prepareStatement(queryDelete)) {
            connection.setAutoCommit(false);
            if (queryDelete.contains("?")) {
                int accountId = account.getId();
                pst.setString(1, account.getEmail());
                pst.setInt(2, accountId);
                pst.setInt(3, accountId);
                pst.setInt(4, accountId);
                pst.setInt(5, accountId);
                pst.setInt(6, accountId);
            }
            pst.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return select("", "Email", account.getEmail());
    }

    public <T> Account delete(String query, List<T> accountData) {
        Account account = null;
        if (accountData.isEmpty()) {
            return null;
        }
        T contact = accountData.get(0);
        if (query.isEmpty()) {
            if (contact instanceof Account.Phone) {
                query = "PHONE WHERE accId=? AND phoneNmr=? AND phoneType=?;";
                account = ((Account.Phone) contact).getAccount();
            } else if (contact instanceof Account.Address) {
                query = "ADDRESS WHERE accId=? AND addr=? AND addrType=?;";
                account = ((Account.Address) contact).getAccount();
            } else if (contact instanceof Account.Messenger) {
                query = "MESSENGER WHERE accId=? AND username=? AND msngrType=?;";
                account = ((Account.Messenger) contact).getAccount();
            } else if (contact instanceof Account.Friend) {
                query = "FRIEND WHERE accId=? AND friendID=?;";
                account = ((Account.Friend) contact).getAccount();
            }
        }

        assert account != null;
        int accountId = account.getId();
        String queryDelete = DELETE + query;
        try (PreparedStatement pst = connection.prepareStatement(queryDelete)) {
            connection.setAutoCommit(false);
            for (T data : accountData) {
                if (queryDelete.contains("?")) {
                    pst.setInt(1, accountId);
                    if (data instanceof Account.Phone) {
                        pst.setString(2, ((Account.Phone) data).getNumber());
                        pst.setString(3, ((Account.Phone) data).getPhoneType().toString());
                    } else if (data instanceof Account.Address) {
                        pst.setString(2, ((Account.Address) data).getAddress());
                        pst.setString(3, ((Account.Address) data).getAddrType().toString());
                    } else if (data instanceof Account.Messenger) {
                        pst.setString(2, ((Account.Messenger) data).getUsername());
                        pst.setString(3, ((Account.Messenger) data).getMsngrType().toString());
                    } else if (data instanceof Account.Friend) {
                        pst.setInt(2, ((Account.Friend) data).getFriendId());
                    }
                }
                pst.executeUpdate();
                connection.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return select("", "Email", account.getEmail());
    }

}