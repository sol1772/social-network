package com.getjavajob.training.maksyutovs.socialnetwork.domain;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class Group implements Serializable {

    private static final long serialVersionUID = 1905122041950251207L;
    private final List<GroupMember> members = new ArrayList<>();
    private int id;
    private int createdBy;
    private String title;
    private String metaTitle;
    private LocalDateTime createdAt;
    private byte[] image;

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return id == group.id && createdBy == group.createdBy && Objects.equals(members, group.members) &&
                title.equals(group.title) && Objects.equals(metaTitle, group.metaTitle) &&
                Objects.equals(createdAt, group.createdAt) && Arrays.equals(image, group.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(members, id, createdBy, title, metaTitle, createdAt);
    }

}