package com.getjavajob.training.maksyutovs.socialnetwork.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class Friend implements Serializable {

    private static final long serialVersionUID = 2905122041950251203L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "accId")
    private Account account;
    @ManyToOne
    @JoinColumn(name = "friendId")
    private Account friendAccount;

    public Friend() {
    }

    public Friend(Account account, Account friendAccount) {
        this.account = account;
        this.friendAccount = friendAccount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getFriendAccount() {
        return friendAccount;
    }

    public void setFriendAccount(Account friendAccount) {
        this.friendAccount = friendAccount;
    }

    public int getAccId() {
        return account.getId();
    }

    public int getFriendId() {
        return friendAccount.getId();
    }

    public String getFriendEmail() {
        return friendAccount.getEmail();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friend friend = (Friend) o;
        return id == friend.id && account.equals(friend.account) && friendAccount.equals(friend.friendAccount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, account, friendAccount);
    }

}
