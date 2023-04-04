package com.getjavajob.training.maksyutovs.socialnetwork.domain;

import java.time.LocalDateTime;

public class Message {

    private Account account;
    private int id;
    private Account targetAccount;
    private MessageType msgType;
    private String txtContent;
    private byte[] mediaContent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Message() {
    }

    public Message(Account account, Account targetAccount, MessageType msgType, String txtContent) {
        this.account = account;
        this.targetAccount = targetAccount;
        this.msgType = msgType;
        this.txtContent = txtContent;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Account getTargetAccount() {
        return targetAccount;
    }

    public void setTargetAccount(Account targetAccount) {
        this.targetAccount = targetAccount;
    }

    public MessageType getMsgType() {
        return msgType;
    }

    public void setMsgType(MessageType msgType) {
        this.msgType = msgType;
    }

    public String getTxtContent() {
        return txtContent;
    }

    public void setTxtContent(String txtContent) {
        this.txtContent = txtContent;
    }

    public byte[] getMediaContent() {
        return mediaContent;
    }

    public void setMediaContent(byte[] mediaContent) {
        this.mediaContent = mediaContent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getAccId() {
        return account.getId();
    }

    public int getTrgtId() {
        return targetAccount.getId();
    }

}
