package com.getjavajob.training.maksyutovs.socialnetwork.domain;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Account {

    private final List<Phone> phones = new ArrayList<>();
    private final List<Address> addresses = new ArrayList<>();
    private final List<Messenger> messengers = new ArrayList<>();
    private final List<Friend> friends = new ArrayList<>();
    private int id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String userName;
    private Date dateOfBirth;
    private Gender gender;
    private String email;
    private String addInfo;
    private String passwordHash;
    private Date registeredAt;

    public Account() {
    }

    public Account(String firstName, String lastName, String userName, Date dateOfBirth, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public List<Messenger> getMessengers() {
        return messengers;
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddInfo() {
        return addInfo;
    }

    public void setAddInfo(String addInfo) {
        this.addInfo = addInfo;
    }

    public Date getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Date registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String hashPassword(String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(password);
    }

    @Override
    public String toString() {
        return userName + "(" + email + ")";
    }

    public enum Gender {
        M, F
    }

    public enum PhoneType {
        PERSONAL, WORK
    }

    public enum AddressType {
        HOME, WORK
    }

    public enum MessengerType {
        SKYPE, TELEGRAM, WHATSAPP, ICQ
    }

    public class Phone {

        private String number;
        private PhoneType phoneType;
        private int id;

        public Phone() {
        }

        public Phone(String number, PhoneType phoneType) {
            this.number = number;
            this.phoneType = phoneType;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public PhoneType getPhoneType() {
            return phoneType;
        }

        public void setPhoneType(PhoneType phoneType) {
            this.phoneType = phoneType;
        }

        public int getAccId() {
            return Account.this.getId();
        }

        public Account getAccount() {
            return Account.this;
        }

    }

    public class Address {

        private String address;
        private AddressType addrType;
        private int id;

        public Address() {
        }

        public Address(String address, AddressType addrType) {
            this.address = address;
            this.addrType = addrType;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public AddressType getAddrType() {
            return addrType;
        }

        public void setAddrType(AddressType addrType) {
            this.addrType = addrType;
        }

        public int getAccId() {
            return Account.this.getId();
        }

        public Account getAccount() {
            return Account.this;
        }

    }

    public class Messenger {

        private String username;
        private MessengerType msngrType;
        private int id;

        public Messenger() {
        }

        public Messenger(String username, MessengerType msngrType) {
            this.username = username;
            this.msngrType = msngrType;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public MessengerType getMsngrType() {
            return msngrType;
        }

        public void setMsngrType(MessengerType msngrType) {
            this.msngrType = msngrType;
        }

        public int getAccId() {
            return Account.this.getId();
        }

        public Account getAccount() {
            return Account.this;
        }

    }

    public class Friend {

        private Account friendAccount;

        public Friend() {
        }

        public Friend(Account friendAccount) {
            this.friendAccount = friendAccount;
        }

        public int getFriendId() {
            return friendAccount.getId();
        }

        public String getFriendEmail() {
            return friendAccount.getEmail();
        }

        public int getAccId() {
            return Account.this.getId();
        }

        public Account getAccount() {
            return Account.this;
        }

    }

}