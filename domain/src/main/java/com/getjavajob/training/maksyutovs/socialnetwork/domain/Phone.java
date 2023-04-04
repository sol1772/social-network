package com.getjavajob.training.maksyutovs.socialnetwork.domain;

public class Phone {

    private final Account account;
    private String number;
    private PhoneType phoneType;
    private int id;

    public Phone(Account account) {
        this.account = account;
    }

    public Phone(Account account, String number, PhoneType phoneType) {
        this.account = account;
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
        return account.getId();
    }

    public Account getAccount() {
        return account;
    }

}
