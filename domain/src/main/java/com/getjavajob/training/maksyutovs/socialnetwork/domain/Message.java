package com.getjavajob.training.maksyutovs.socialnetwork.domain;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

@Component
public class Message implements Serializable {

    private static final long serialVersionUID = 6470090944414208496L;
    private Account account;
    private int id;
    private Account targetAccount;
    private transient Group targetGroup;
    private MessageType msgType;
    private String textContent;
    private byte[] mediaContent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Message() {
    }

    public Message(Account account, MessageType msgType, String txtContent) {
        this.account = account;
        this.msgType = msgType;
        this.textContent = txtContent;
    }

    public Message(Account account, Account targetAccount, MessageType msgType, String txtContent) {
        this.account = account;
        this.targetAccount = targetAccount;
        this.msgType = msgType;
        this.textContent = txtContent;
    }

    public Message(Account account, Group targetGroup, MessageType msgType, String txtContent) {
        this.account = account;
        this.targetGroup = targetGroup;
        this.msgType = msgType;
        this.textContent = txtContent;
    }

    public Message(Account account, Account targetAccount, Group targetGroup, MessageType msgType, String textContent,
                   byte[] mediaContent, LocalDateTime createdAt) {
        this.account = account;
        this.targetAccount = targetAccount;
        this.targetGroup = targetGroup;
        this.msgType = msgType;
        this.textContent = textContent;
        this.mediaContent = mediaContent;
        this.createdAt = createdAt;
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

    public Group getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(Group targetGroup) {
        this.targetGroup = targetGroup;
    }

    public MessageType getMsgType() {
        return msgType;
    }

    public void setMsgType(MessageType msgType) {
        this.msgType = msgType;
    }

    public String getTextContent() {
        return this.textContent;
    }

    public void setTextContent(String txtContent) {
        this.textContent = txtContent;
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

    public int getTrgId() {
        return targetAccount.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id == message.id && Objects.equals(account, message.account) &&
                Objects.equals(targetAccount, message.targetAccount) &&
                Objects.equals(targetGroup, message.targetGroup) && msgType == message.msgType &&
                Objects.equals(textContent, message.textContent) && Arrays.equals(mediaContent, message.mediaContent) &&
                Objects.equals(createdAt, message.createdAt) && Objects.equals(updatedAt, message.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, id, targetAccount, targetGroup, msgType, textContent, createdAt, updatedAt);
    }

}
