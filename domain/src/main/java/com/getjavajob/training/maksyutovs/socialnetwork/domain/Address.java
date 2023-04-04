package com.getjavajob.training.maksyutovs.socialnetwork.domain;

public class Address {

    private final Account account;
    private String address;
    private AddressType addrType;
    private int id;

    public Address(Account account) {
        this.account = account;
    }

    public Address(Account account, String address, AddressType addrType) {
        this.account = account;
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
        return account.getId();
    }

    public Account getAccount() {
        return account;
    }

}
