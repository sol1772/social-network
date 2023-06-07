package com.getjavajob.training.maksyutovs.socialnetwork.domain;

import java.io.Serializable;

public class Messenger implements Serializable {

    private static final long serialVersionUID = 4505122041950251272L;
    private final Account account;
    private String username;
    private MessengerType msgrType;
    private int id;

    public Messenger(Account account) {
        this.account = account;
    }

    public Messenger(Account account, String username, MessengerType msgrType) {
        this.account = account;
        this.username = username;
        this.msgrType = msgrType;
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

    public int getAccId() {
        return account.getId();
    }

    public Account getAccount() {
        return account;
    }

}
