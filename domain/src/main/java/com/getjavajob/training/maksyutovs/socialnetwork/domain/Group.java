package com.getjavajob.training.maksyutovs.socialnetwork.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Group {

    private final List<GroupMember> members = new ArrayList<>();
    private int id;
    private int createdBy;
    private String title;
    private String metaTitle;
    private Date createdAt;

    public Group() {
    }

    public Group(String title) {
        this.title = title;
    }

    public List<GroupMember> getMembers() {
        return members;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return title;
    }

    public enum Role {
        ADMIN, MODER, MEMBER
    }

    public class GroupMember {

        private int id;
        private com.getjavajob.training.maksyutovs.socialnetwork.domain.Account account;
        private Role role;

        public GroupMember() {
        }

        public GroupMember(com.getjavajob.training.maksyutovs.socialnetwork.domain.Account account, Role role) {
            this.account = account;
            this.role = role;
        }

        public com.getjavajob.training.maksyutovs.socialnetwork.domain.Account getAccount() {
            return account;
        }

        public Role getRole() {
            return role;
        }

        public int getGroupId() {
            return Group.this.getId();
        }

        public Group getGroup() {
            return Group.this;
        }

    }

}