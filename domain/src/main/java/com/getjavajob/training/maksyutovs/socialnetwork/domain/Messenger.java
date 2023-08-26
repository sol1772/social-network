package com.getjavajob.training.maksyutovs.socialnetwork.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class Messenger implements Serializable {

    private static final long serialVersionUID = 4505122041950251272L;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "accId")
    private Account account;
    private String username;
    @Column(columnDefinition = "enum")
    @Enumerated(EnumType.STRING)
    private MessengerType msgrType;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public Messenger() {
    }

    public Messenger(Account account) {
        this.account = account;
    }

    public Messenger(Account account, int id, String username, MessengerType msgrType) {
        this.account = account;
        this.id = id;
        this.username = username;
        this.msgrType = msgrType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public MessengerType getMsgrType() {
        return msgrType;
    }

    public void setMsgrType(MessengerType msgrType) {
        this.msgrType = msgrType;
    }

    @XmlTransient
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
        Messenger messenger = (Messenger) o;
        return id == messenger.id && Objects.equals(account, messenger.account) &&
                username.equals(messenger.username) && msgrType == messenger.msgrType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, username, msgrType, id);
    }

}
