package com.getjavajob.training.maksyutovs.socialnetwork.domain;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Account implements Serializable {

    private final List<Phone> phones = new ArrayList<>();
    private final List<Address> addresses = new ArrayList<>();
    private final List<Messenger> messengers = new ArrayList<>();
    private final List<Friend> friends = new ArrayList<>();
    private final List<Message> messages = new ArrayList<>();
    private int id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String userName;
    private Date dateOfBirth;
    private Gender gender;
    private String email;
    private String passwordHash;
    private String addInfo;
    private Date registeredAt;
    private byte[] image;

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

    public List<Message> getMessages() {
        return messages;
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
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
        return userName + " (" + email + ")";
    }

}