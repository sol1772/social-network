package com.getjavajob.training.maksyutovs.socialnetwork.domain.dto;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AccountDto {

    private final List<Phone> phones = new ArrayList<>();
    private final List<Address> addresses = new ArrayList<>();
    private final List<Messenger> messengers = new ArrayList<>();
    private int id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String userName;
    @DateTimeFormat(pattern = Utils.DATE_PATTERN)
    private LocalDate dateOfBirth;
    private Gender gender;
    private String email;
    private String password;
    private String addInfo;
    private String personalPhone;
    private String workPhone;
    private String homeAddress;
    private String workAddress;

    public AccountDto() {
    }

    public AccountDto(int id, String firstName, String lastName, String userName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
    }

    public AccountDto(String firstName, String middleName, String lastName, String userName,
                      LocalDate dateOfBirth, Gender gender, String email) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.userName = userName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddInfo() {
        return addInfo;
    }

    public void setAddInfo(String addInfo) {
        this.addInfo = addInfo;
    }

    public String getPersonalPhone() {
        return personalPhone;
    }

    public void setPersonalPhone(String personalPhone) {
        this.personalPhone = personalPhone;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
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

}
