package com.getjavajob.training.maksyutovs.socialnetwork.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class Address implements Serializable {

    private static final long serialVersionUID = 1905133041950251207L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "accId")
    private Account account;
    private String addr;
    @Column(columnDefinition = "enum")
    @Enumerated(EnumType.STRING)
    private AddressType addrType;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return id == address.id && Objects.equals(account, address.account) &&
                addr.equals(address.addr) && addrType == address.addrType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, addr, addrType, id);
    }

}
