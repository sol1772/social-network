package com.getjavajob.training.maksyutovs.socialnetwork.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class Phone implements Serializable {

    private static final long serialVersionUID = 1105122041958251257L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accId")
    private Account account;
    @Column(name = "phoneNmr")
    private String number;
    @Column(columnDefinition = "enum")
    @Enumerated(EnumType.STRING)
    private PhoneType phoneType;

    public Phone() {
    }

    public Phone(Account account) {
        this.account = account;
    }

    public Phone(Account account, int id, String number, PhoneType phoneType) {
        this.account = account;
        this.id = id;
        this.number = number;
        this.phoneType = phoneType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public int getAccId() {
        return account.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Phone phone = (Phone) o;
        return id == phone.id && Objects.equals(account, phone.account) &&
                number.equals(phone.number) && phoneType == phone.phoneType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, number, phoneType, id);
    }

}
