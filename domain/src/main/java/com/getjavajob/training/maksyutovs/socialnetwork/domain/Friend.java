package com.getjavajob.training.maksyutovs.socialnetwork.domain;

public class Friend {

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

}
