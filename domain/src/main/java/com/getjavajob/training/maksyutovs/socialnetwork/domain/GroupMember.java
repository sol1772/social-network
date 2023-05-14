package com.getjavajob.training.maksyutovs.socialnetwork.domain;

public class GroupMember {

    private final Group group;
    private Account account;
    private Role role;
    private int id;

    public GroupMember(Group group) {
        this.group = group;
    }

    public GroupMember(Group group, Account account, Role role) {
        this.group = group;
        this.account = account;
        this.role = role;
    }

    public Account getAccount() {
        return account;
    }

    public Role getRole() {
        return role;
    }

    public int getGroupId() {
        return group.getId();
    }

    public Group getGroup() {
        return group;
    }

}
