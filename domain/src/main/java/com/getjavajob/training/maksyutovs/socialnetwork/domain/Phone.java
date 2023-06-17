package com.getjavajob.training.maksyutovs.socialnetwork.domain;

import java.io.Serializable;

public class Phone implements Serializable {

    private static final long serialVersionUID = 1105122041958251257L;
    private Account account;
    private String number;
    private PhoneType phoneType;
    private int id;

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

}
