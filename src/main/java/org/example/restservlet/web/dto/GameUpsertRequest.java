package org.example.restservlet.web.dto;

public class GameUpsertRequest {

    private String title;

    private Long publisherId;

    public GameUpsertRequest() {
    }

    public GameUpsertRequest(String title, Long publisherId) {
        this.title = title;
        this.publisherId = publisherId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }
}


