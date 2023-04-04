package com.getjavajob.training.maksyutovs.socialnetwork.domain;

public class Messenger {

    private final Account account;
    private String username;
    private MessengerType msngrType;
    private int id;

    public Messenger(Account account) {
        this.account = account;
    }

    public Messenger(Account account, String username, MessengerType msngrType) {
        this.account = account;
        this.username = username;
        this.msngrType = msngrType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public MessengerType getMsngrType() {
        return msngrType;
    }

    public void setMsngrType(MessengerType msngrType) {
        this.msngrType = msngrType;
    }

    public int getAccId() {
        return account.getId();
    }

    public Account getAccount() {
        return account;
    }

}
