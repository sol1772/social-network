package com.getjavajob.training.maksyutovs.socialnetwork.domain;

import java.io.Serializable;

public class Messenger implements Serializable {

    private static final long serialVersionUID = 4505122041950251272L;
    private Account account;
    private String username;
    private MessengerType msgrType;
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
