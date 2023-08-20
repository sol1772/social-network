package com.getjavajob.training.maksyutovs.socialnetwork.domain.dto;

public class GroupDto {

    private int id;
    private String title;
    private String metaTitle;

    public GroupDto() {
    }

    public GroupDto(int id, String title, String metaTitle) {
        this.id = id;
        this.title = title;
        this.metaTitle = metaTitle;
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

}
