package com.getjavajob.training.maksyutovs.socialnetwork.domain;

import java.io.Serializable;

public class Address implements Serializable {

    private static final long serialVersionUID = 1905133041950251207L;
    private Account account;
    private String addr;
    private AddressType addrType;
    private int id;

    public Address() {
    }

    public Address(Account account) {
        this.account = account;
    }

    public Address(Account account, int id, String address, AddressType addrType) {
        this.account = account;
        this.id = id;
        this.addr = address;
        this.addrType = addrType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public AddressType getAddrType() {
        return addrType;
    }

    public void setAddrType(AddressType addrType) {
        this.addrType = addrType;
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
