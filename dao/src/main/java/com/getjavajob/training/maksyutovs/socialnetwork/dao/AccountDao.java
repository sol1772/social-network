package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AccountDao implements CrudDao<Account, Object> {

    private static final String CREATE = "INSERT INTO ";
    private static final String READ = "SELECT * FROM ";
    private static final String UPDATE = "UPDATE ";
    private static final String DELETE = "DELETE FROM ";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String USERNAME = "username";
    private static final String BIRTHDATE = "dateOfBirth";
    private static final String EMAIL = "email";
    private static final String ID = "id";
    private JdbcTemplate jdbcTemplate;

    public AccountDao() {
    }

    @Autowired
    public AccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    Account createAccountFromResult(ResultSet rs) throws SQLException {
        Account account;
        account = new Account(rs.getString(FIRST_NAME), rs.getString(LAST_NAME), rs.getString(USERNAME),
                rs.getDate(BIRTHDATE).toLocalDate(), rs.getString(EMAIL));
        account.setId(rs.getInt("id"));
        account.setPasswordHash(rs.getString("passwordHash"));
        account.setMiddleName(rs.getString("middleName"));
        account.setGender(rs.getString("gender") == null ?
                Gender.M : Gender.valueOf(rs.getString("gender")));
        account.setAddInfo(rs.getString("addInfo"));
        account.setRegisteredAt(rs.getTimestamp("registeredAt").toLocalDateTime());
        account.setImage(rs.getBytes("image"));
        return account;
    }

    Phone createPhoneFromResult(ResultSet rs, Account account) throws SQLException {
        return new Phone(account, rs.getInt("id"), rs.getString("phoneNmr"),
                PhoneType.valueOf(rs.getString("phoneType")));
    }

    Address createAddressFromResult(ResultSet rs, Account account) throws SQLException {
        return new Address(account, rs.getInt("id"), rs.getString("addr"),
                AddressType.valueOf(rs.getString("addrType")));
    }

    Messenger createMessengerFromResult(ResultSet rs, Account account) throws SQLException {
        return new Messenger(account, rs.getInt("id"), rs.getString(USERNAME),
                MessengerType.valueOf(rs.getString("msgrType")));
    }

    Friend createFriendFromResult(ResultSet rs, Account account) throws SQLException {
        Account friendAccount = createAccountFromResult(rs);
        return new Friend(account, friendAccount);
    }

    Message createMessageFromResult(ResultSet rs, Account account, Account targetAccount) throws SQLException {
        Message message = new Message(account, targetAccount, null,
                MessageType.valueOf(rs.getString("msgType")),
                rs.getString("txtContent"), null,
                rs.getTimestamp("createdAt").toLocalDateTime());
        message.setId(rs.getInt("id"));
        return message;
    }

    Account getAccountData(Account account) {
        // contacts, friends
        String queryPhone = READ + "Phone WHERE accId=?;";
        String queryAddress = READ + "Address WHERE accId=?;";
        String queryMessenger = READ + "Messenger WHERE accId=?;";
        String queryFriend = READ + "Account a INNER JOIN Friend f ON a.id = f.friendId AND f.accId=?;";
        String queryMessage = READ + "Message m INNER JOIN Account a ON m.trgId = a.id WHERE accId=?;";

        account.getPhones().addAll(jdbcTemplate.query(queryPhone, (rs, rowNum) ->
                createPhoneFromResult(rs, account), account.getId()));

        account.getAddresses().addAll(jdbcTemplate.query(queryAddress, (rs, rowNum) ->
                createAddressFromResult(rs, account), account.getId()));

        account.getMessengers().addAll(jdbcTemplate.query(queryMessenger, (rs, rowNum) ->
                createMessengerFromResult(rs, account), account.getId()));

        account.getFriends().addAll(jdbcTemplate.query(queryFriend, (rs, rowNum) ->
                createFriendFromResult(rs, account), account.getId()));

        account.getMessages().addAll(jdbcTemplate.query(queryMessage, (rs, rowNum) ->
                createMessageFromResult(rs, account, createAccountFromResult(rs)), account.getId()));
        return account;
    }

    @Override
    public Account insert(Account account) {
        String queryInsert = CREATE + "Account(firstName,middleName,lastName,username,email," +
                "dateOfBirth,gender,addInfo,passwordHash,registeredAt,image)" +
                " VALUES (?,?,?,?,?,?,?,?,?,now(),?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement pst = con.prepareStatement(queryInsert, new String[]{"id"});
            pst.setString(1, account.getFirstName());
            pst.setString(2, account.getMiddleName());
            pst.setString(3, account.getLastName());
            pst.setString(4, account.getUserName());
            pst.setString(5, account.getEmail());
            pst.setString(6, Utils.DATE_FORMATTER.format(account.getDateOfBirth()));
            pst.setString(7, String.valueOf(account.getGender() != null ?
                    account.getGender().toString().charAt(0) : 'M'));
            pst.setString(8, account.getAddInfo());
            pst.setString(9, account.getPasswordHash());
            pst.setBytes(10, account.getImage());
            return pst;
        }, keyHolder);
        return select(ID, keyHolder.getKey());
    }

    public <T> Account insert(List<T> accountData) {
        if (accountData.isEmpty()) {
            throw new IllegalArgumentException("No data to insert");
        }

        T contact = accountData.get(0);
        Account account = getAccountFromAccountData(contact);
        String query = "";
        if (contact instanceof Phone) {
            query = "Phone(accId,phoneNmr,phoneType) VALUES (?,?,?);";
        } else if (contact instanceof Address) {
            query = "Address(accId,addr,addrType) VALUES (?,?,?);";
        } else if (contact instanceof Messenger) {
            query = "Messenger(accId,username,msgrType) VALUES (?,?,?);";
        } else if (contact instanceof Friend) {
            query = "Friend(accId,friendId) VALUES (?,?), (?,?);";
        } else if (contact instanceof Message) {
            query = "Message(accId,trgId,txtContent,mediaContent,msgType,createdAt) VALUES (?,?,?,?,?,now());";
        }
        assert account != null;
        int accountId = account.getId();
        for (T data : accountData) {
            String queryInsert = CREATE + query;
            jdbcTemplate.update(con -> {
                PreparedStatement pst = con.prepareStatement(queryInsert);
                pst.setInt(1, accountId);
                if (data instanceof Phone) {
                    pst.setString(2, ((Phone) data).getNumber());
                    pst.setString(3, ((Phone) data).getPhoneType().toString());
                } else if (data instanceof Address) {
                    pst.setString(2, ((Address) data).getAddr());
                    pst.setString(3, ((Address) data).getAddrType().toString());
                } else if (data instanceof Messenger) {
                    pst.setString(2, ((Messenger) data).getUsername());
                    pst.setString(3, ((Messenger) data).getMsgrType().toString());
                } else if (data instanceof Friend) {
                    pst.setInt(2, ((Friend) data).getFriendId());
                    pst.setInt(3, ((Friend) data).getFriendId());
                    pst.setInt(4, accountId);
                } else if (data instanceof Message) {
                    pst.setInt(2, ((Message) data).getTrgId());
                    pst.setString(3, ((Message) data).getTextContent());
                    pst.setBytes(4, ((Message) data).getMediaContent());
                    pst.setString(5, ((Message) data).getMsgType().toString());
                }
                return pst;
            });
        }
        return select(EMAIL, account.getEmail());
    }

    private <T> Account getAccountFromAccountData(T contact) {
        Account account = null;
        if (contact instanceof Phone) {
            account = ((Phone) contact).getAccount();
        } else if (contact instanceof Address) {
            account = ((Address) contact).getAccount();
        } else if (contact instanceof Messenger) {
            account = ((Messenger) contact).getAccount();
        } else if (contact instanceof Friend) {
            account = ((Friend) contact).getAccount();
        } else if (contact instanceof Message) {
            account = ((Message) contact).getAccount();
        }
        return account;
    }

    @Override
    public Account select(String field, Object value) {
        String querySelect = READ + "Account WHERE " + field + "=?;";
        Account dbAccount = jdbcTemplate.query(querySelect, (rs, rowNum) -> createAccountFromResult(rs), value)
                .stream().findAny().orElse(null);
        if (dbAccount != null) {
            dbAccount = getAccountData(dbAccount);
        }
        return dbAccount;
    }

    public boolean selectByEmail(String email) {
        String querySelect = READ + "Account WHERE email=?;";
        return jdbcTemplate.query(querySelect, (rs, rowNum) -> true, email)
                .stream().findAny().orElse(false);
    }

    public <T> Object selectByValueAndType(String value, T type, Account account) {
        String query;
        int accId = account.getId();
        if (type instanceof PhoneType) {
            query = READ + "Phone WHERE accId=? AND phoneNmr LIKE ? AND phoneType=?;";
            return jdbcTemplate.queryForObject(query, (rs, rowNum) -> createPhoneFromResult(rs, account),
                    accId, "%" + value + "%", type.toString());
        } else if (type instanceof Address) {
            query = READ + "Address WHERE accId=? AND addr LIKE ? AND addrType=?;";
            return jdbcTemplate.query(query, (rs, rowNum) -> createAddressFromResult(rs, account),
                    accId, "%" + value + "%", type.toString()).stream().findAny().orElse(null);
        } else if (type instanceof Messenger) {
            query = READ + "Messenger WHERE accId=? AND username LIKE ? AND msgrType=?;";
            return jdbcTemplate.query(query, (rs, rowNum) -> createMessengerFromResult(rs, account),
                    accId, "%" + value + "%", type.toString()).stream().findAny().orElse(null);
        } else if (type instanceof Message) {
            query = READ + "Message m INNER JOIN Account a ON m.trgId = a.id " +
                    "WHERE accId=? AND txtContent LIKE ? AND msgType=?;";
            return jdbcTemplate.query(query, (rs, rowNum) -> createMessageFromResult(rs, account,
                            createAccountFromResult(rs)), accId, "%" + value + "%", type.toString())
                    .stream().findAny().orElse(null);
        }
        return null;
    }

    public List<Account> selectAll() {
        String querySelect = READ + "Account";
        return jdbcTemplate.query(querySelect, (rs, rowNum) -> createAccountFromResult(rs));
    }

    public List<Account> selectByString(String substring, int start, int total) {
        String searchString = "%" + substring + "%";
        String querySelect = READ + "Account WHERE firstName LIKE ? OR lastName LIKE ? ORDER BY lastName" +
                (total > 0 ? " LIMIT " + (start - 1) + "," + total : "");
        return jdbcTemplate.query(querySelect, (rs, rowNum) -> createAccountFromResult(rs), searchString, searchString);
    }

    public Integer selectCountByString(String substring, int start, int total) {
        String searchString = "%" + substring + "%";
        String querySelect = READ + "Account WHERE firstName LIKE ? OR lastName LIKE ? ORDER BY lastName" +
                (total > 0 ? " LIMIT " + (start - 1) + "," + total : "");
        querySelect = querySelect.replace("SELECT *", "SELECT COUNT(*) AS count");
        return jdbcTemplate.queryForObject(querySelect, Integer.class, searchString, searchString);
    }

    public List<Account> selectTargetAccounts(Account account, MessageType type) {
        String querySelect;
        if (type == null) {
            querySelect = "SELECT a.* FROM Account a INNER JOIN Message m ON a.id = m.trgId" +
                    " WHERE m.accId=? UNION " +
                    " SELECT a.* FROM Account a INNER JOIN Message m ON a.id = m.accId" +
                    " WHERE m.trgId=? GROUP BY a.id;";
        } else {
            querySelect = "SELECT a.* FROM Account a INNER JOIN Message m ON a.id = m.trgId" +
                    " WHERE m.accId=? AND m.msgType=? UNION " +
                    " SELECT a.* FROM Account a INNER JOIN Message m ON a.id = m.accId" +
                    " WHERE m.trgId=? AND m.msgType=? GROUP BY a.id;";
        }
        if (type == null) {
            return jdbcTemplate.query(querySelect, (rs, rowNum) -> createAccountFromResult(rs),
                    account.getId(), account.getId());
        } else {
            return jdbcTemplate.query(querySelect, (rs, rowNum) -> createAccountFromResult(rs),
                    account.getId(), type.toString(), account.getId(), type.toString());
        }
    }

    public List<Message> selectMessages(Account account, Account targetAccount, MessageType type) {
        List<Message> messages = new ArrayList<>();
        String querySelect;
        if (type == null) {
            return messages;
        } else {
            querySelect = "SELECT * FROM Message WHERE (accId=? AND trgId=?) " +
                    "OR (accId=? AND trgId=?) AND msgType=? ORDER BY createdAt DESC;";
        }
        int accId = account.getId();
        int trgId = targetAccount.getId();
        return jdbcTemplate.query(querySelect, (rs, rowNum) -> (rs.getInt("accId") == accId) ?
                        createMessageFromResult(rs, account, targetAccount) :
                        createMessageFromResult(rs, targetAccount, account),
                accId, trgId, trgId, accId, type.toString());
    }

    @Override
    public Account update(String field, Object value, Account account) {
        String queryUpdate = UPDATE + "Account SET " + field + "=? WHERE email=?;";
        jdbcTemplate.update(con -> {
            PreparedStatement pst = con.prepareStatement(queryUpdate);
            if (value instanceof String) {
                pst.setString(1, (String) value);
            } else if (value instanceof Integer) {
                pst.setInt(1, (int) value);
            } else if (value instanceof InputStream) {
                pst.setBinaryStream(1, (InputStream) value);
            }
            pst.setString(2, account.getEmail());
            return pst;
        });
        return select(EMAIL, account.getEmail());
    }

    public Account update(Account account) {
        String queryUpdate = UPDATE + "Account SET firstName=?,middleName=?,lastName=?,username=?," +
                "dateOfBirth=?,gender=?,addInfo=? WHERE email=?;";
        jdbcTemplate.update(con -> {
            PreparedStatement pst = con.prepareStatement(queryUpdate);
            pst.setString(1, account.getFirstName());
            pst.setString(2, account.getMiddleName());
            pst.setString(3, account.getLastName());
            pst.setString(4, account.getUserName());
            pst.setString(5, Utils.DATE_FORMATTER.format(account.getDateOfBirth()));
            pst.setString(6, String.valueOf(account.getGender() != null ?
                    account.getGender().toString().charAt(0) : 'M'));
            pst.setString(7, account.getAddInfo());
            pst.setString(8, account.getEmail());
            return pst;
        });
        return select(EMAIL, account.getEmail());
    }

    public <T> Account updateAccountData(String value, T type, int id, Account account) {
        String query = "";
        if (type instanceof PhoneType) {
            query = "Phone SET phoneNmr=?,phoneType=? WHERE id=?;";
        } else if (type instanceof Address) {
            query = "Address SET addr=?,addrType=? WHERE id=?;";
        } else if (type instanceof Messenger) {
            query = "Messenger SET username=?,msgrType=? WHERE id=?;";
        } else if (type instanceof Message) {
            query = "Message SET txtContent=?,msgType=?,updatedAt=now() WHERE id=?);";
        }
        String queryUpdate = UPDATE + query;
        jdbcTemplate.update(con -> {
            PreparedStatement pst = con.prepareStatement(queryUpdate);
            pst.setString(1, value);
            pst.setString(2, type.toString());
            pst.setInt(3, id);
            return pst;
        });
        return select(EMAIL, account.getEmail());
    }

    @Override
    public Account delete(Account account) {
        List<String> queries = new ArrayList<>();
        queries.add("Account WHERE email=?;");
        queries.add("Phone WHERE accId=?;");
        queries.add("Address WHERE accId=?;");
        queries.add("Messenger WHERE accId=?;");
        queries.add("Friend WHERE accId=? OR friendId=?;");
        queries.add("Message WHERE accId=? OR trgId=?");
        for (String queryText : queries) {
            String queryDelete = DELETE + queryText;
            jdbcTemplate.update(con -> {
                PreparedStatement pst = con.prepareStatement(queryDelete);
                if (queryDelete.contains("Account")) {
                    pst.setString(1, account.getEmail());
                } else {
                    pst.setInt(1, account.getId());
                    if (queryDelete.contains("Friend") || queryDelete.contains("Message")) {
                        pst.setInt(2, account.getId());
                    }
                }
                return pst;
            });
        }
        return select(EMAIL, account.getEmail());
    }

    public <T> Account delete(List<T> accountData) {
        if (accountData.isEmpty()) {
            throw new IllegalArgumentException("No data to delete");
        }

        T contact = accountData.get(0);
        Account account = getAccountFromAccountData(contact);
        String query = "";
        if (contact instanceof Phone) {
            query = "Phone WHERE accId=? AND phoneNmr LIKE ? AND phoneType=?;";
        } else if (contact instanceof Address) {
            query = "Address WHERE accId=? AND addr LIKE ? AND addrType=?;";
        } else if (contact instanceof Messenger) {
            query = "Messenger WHERE accId=? AND username LIKE ? AND msgrType=?;";
        } else if (contact instanceof Friend) {
            query = "Friend WHERE accId=? AND friendId=?;";
        } else if (contact instanceof Message) {
            query = "Message WHERE accId=? AND trgId=? AND id=?;";
        }

        assert account != null;
        int accountId = account.getId();
        String queryDelete = DELETE + query;
        for (T data : accountData) {
            jdbcTemplate.update(con -> {
                PreparedStatement pst = con.prepareStatement(queryDelete);
                pst.setInt(1, accountId);
                if (data instanceof Phone) {
                    pst.setString(2, ((Phone) data).getNumber());
                    pst.setString(3, ((Phone) data).getPhoneType().toString());
                } else if (data instanceof Address) {
                    pst.setString(2, ((Address) data).getAddr());
                    pst.setString(3, ((Address) data).getAddrType().toString());
                } else if (data instanceof Messenger) {
                    pst.setString(2, ((Messenger) data).getUsername());
                    pst.setString(3, ((Messenger) data).getMsgrType().toString());
                } else if (data instanceof Friend) {
                    pst.setInt(2, ((Friend) data).getFriendId());
                } else if (data instanceof Message) {
                    pst.setInt(2, ((Message) data).getTrgId());
                    pst.setInt(3, ((Message) data).getId());
                }
                return pst;
            });
        }
        return select(EMAIL, account.getEmail());
    }

    public boolean insertMessage(Message message) {
        String queryInsert = CREATE + "Message(accId,trgId,txtContent,msgType,createdAt) VALUES (?,?,?,?,now());";
        jdbcTemplate.update(queryInsert, message.getAccount().getId(), message.getTargetAccount().getId(),
                message.getTextContent(), message.getMsgType().toString());
        return true;
    }

    public boolean deleteMessageById(int id) {
        String queryInsert = DELETE + "Message WHERE id=?;";
        jdbcTemplate.update(queryInsert, id);
        return true;
    }

}