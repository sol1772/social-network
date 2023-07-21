package com.getjavajob.training.maksyutovs.socialnetwork.domain;

import java.io.Serializable;
import java.util.Objects;

public class Friend implements Serializable {

    private static final long serialVersionUID = 2905122041950251203L;
    private final Account account;
    private Account friendAccount;

    public Friend(Account account) {
        this.account = account;
    }

    public Friend(Account account, Account friendAccount) {
        this.account = account;
        this.friendAccount = friendAccount;
    }

    public int getFriendId() {
        return friendAccount.getId();
    }

    public String getFriendEmail() {
        return friendAccount.getEmail();
    }

    public int getAccId() {
        return account.getId();
    }

    public Account getAccount() {
        return account;
    }

    public Account getFriendAccount() {
        return friendAccount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friend friend = (Friend) o;
        return Objects.equals(account, friend.account) && Objects.equals(friendAccount, friend.friendAccount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, friendAccount);
    }

}
