package com.getjavajob.training.maksyutovs.socialnetwork.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "InterestGroup")
public class Group implements Serializable {

    private static final long serialVersionUID = 1905122041950251207L;
    @JsonManagedReference
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private final List<GroupMember> members = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int createdBy;
    @NaturalId
    @Column(nullable = false, unique = true)
    @NotEmpty(message = "Title should not be empty")
    private String title;
    private String metaTitle;
    @CreationTimestamp
    @DateTimeFormat(pattern = Utils.DATE_TIME_PATTERN)
    private LocalDateTime createdAt;
    @Lob
    @Column(columnDefinition = "blob", length = 65535)
    private byte[] image;

    public Group() {
    }

    public Group(String title) {
        this.title = title;
    }

    public Group(int id, String title, String metaTitle) {
        this.id = id;
        this.title = title;
        this.metaTitle = metaTitle;
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