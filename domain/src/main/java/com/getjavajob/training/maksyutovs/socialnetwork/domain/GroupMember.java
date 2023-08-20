package com.getjavajob.training.maksyutovs.socialnetwork.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "Group_member")
public class GroupMember implements Serializable {

    private static final long serialVersionUID = 2405172041950252207L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupId")
    private Group group;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "accId")
    private Account account;
    @Column(name = "roleType", columnDefinition = "enum")
    @Enumerated(EnumType.STRING)
    private Role role;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupMember that = (GroupMember) o;
        return id == that.id && Objects.equals(group, that.group) &&
                Objects.equals(account, that.account) && role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, account, role, id);
    }

}
