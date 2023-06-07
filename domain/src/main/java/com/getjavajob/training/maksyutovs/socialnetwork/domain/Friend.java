package com.getjavajob.training.maksyutovs.socialnetwork.domain;

import java.io.Serializable;

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

}
