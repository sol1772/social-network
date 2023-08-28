package com.getjavajob.training.maksyutovs.socialnetwork.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@XmlRootElement(name = "account")
@XmlType(propOrder = {"id", "firstName", "middleName", "lastName", "userName", "dateOfBirth", "gender", "email",
        "addInfo", "phones", "addresses", "messengers"})
@Entity
public class Account implements Serializable {

    private static final long serialVersionUID = 1905122041950251207L;
    @JsonManagedReference
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private final List<Phone> phones = new ArrayList<>();
    @JsonManagedReference
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private final List<Address> addresses = new ArrayList<>();
    @JsonManagedReference
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private final List<Messenger> messengers = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotEmpty(message = "First name should not be empty")
    private String firstName;
    private String middleName;
    @NotEmpty(message = "Last name should not be empty")
    private String lastName;
    private String userName;
    @DateTimeFormat(pattern = Utils.DATE_PATTERN)
    private LocalDate dateOfBirth;
    @Column(columnDefinition = "enum")
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @NaturalId
    @Column(nullable = false, unique = true)
    @NotEmpty(message = "Email should not be empty")
    private String email;
    private String passwordHash;
    private String addInfo;
    @CreationTimestamp
    @DateTimeFormat(pattern = Utils.DATE_TIME_PATTERN)
    private LocalDateTime registeredAt;
    @Lob
    @Column(columnDefinition = "blob", length = 65535)
    private byte[] image;

    public Account() {
    }

    public Account(String firstName, String lastName, String userName, LocalDate dateOfBirth, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    @XmlAttribute
    public void setId(int id) {
        this.id = id;
    }

    @XmlElement
    public List<Phone> getPhones() {
        return phones;
    }

    @XmlElement
    public List<Address> getAddresses() {
        return addresses;
    }

    @XmlElement
    public List<Messenger> getMessengers() {
        return messengers;
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

    @XmlJavaTypeAdapter(value = Utils.class)
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

    public String getAddInfo() {
        return addInfo;
    }

    public void setAddInfo(String addInfo) {
        this.addInfo = addInfo;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    @XmlTransient
    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public byte[] getImage() {
        return image;
    }

    @XmlTransient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    @XmlTransient
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id == account.id && Objects.equals(phones, account.phones) &&
                Objects.equals(addresses, account.addresses) && Objects.equals(messengers, account.messengers) &&
                firstName.equals(account.firstName) && Objects.equals(middleName, account.middleName) &&
                lastName.equals(account.lastName) && userName.equals(account.userName) &&
                dateOfBirth.equals(account.dateOfBirth) && gender == account.gender && email.equals(account.email) &&
                Objects.equals(passwordHash, account.passwordHash) && Objects.equals(addInfo, account.addInfo) &&
                Objects.equals(registeredAt, account.registeredAt) && Arrays.equals(image, account.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phones, addresses, messengers, id, firstName, middleName, lastName,
                userName, dateOfBirth, gender, email, passwordHash, addInfo, registeredAt);
    }

}