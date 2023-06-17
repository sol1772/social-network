package com.getjavajob.training.maksyutovs.socialnetwork.domain;

import java.io.Serializable;

public class GroupMember implements Serializable {

    private static final long serialVersionUID = 2405172041950252207L;
    private Group group;
    private Account account;
    private Role role;
    private int id;

    public GroupMember() {
    }

    public GroupMember(Group group) {
        this.group = group;
    }

    public GroupMember(Group group, Account account, Role role) {
        this.group = group;
        this.account = account;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public int getGroupId() {
        return group.getId();
    }

}
