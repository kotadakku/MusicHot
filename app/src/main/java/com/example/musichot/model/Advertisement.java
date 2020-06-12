package com.example.musichot.model;

public class Advertisement {
    String id, content, idsong, image;

    public Advertisement(String id, String content, String idsong, String image) {
        this.id = id;
        this.content = content;
        this.idsong = idsong;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIdsong() {
        return idsong;
    }

    public void setIdsong(String idsong) {
        this.idsong = idsong;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
