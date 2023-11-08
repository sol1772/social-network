package com.getjavajob.training.maksyutovs.socialnetwork.domain;

public class ChatMessage {

    private MessageType type;
    private String sender;
    private String content;

    public ChatMessage() {
    }

    public ChatMessage(MessageType type, String sender) {
        this.type = type;
        this.sender = sender;
    }

    public ChatMessage(MessageType type, String sender, String content) {
        this.type = type;
        this.sender = sender;
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }

}
