package org.example.restservlet.web.dto;

import java.util.List;

public class GameUsersResponse {

    private Long id;

    private String title;

    private PublisherResponse publisher;

    private List<UserResponse> users;

    public GameUsersResponse() {
    }

    public GameUsersResponse(Long id, String title, PublisherResponse publisherResponse, List<UserResponse> users) {
        this.id = id;
        this.title = title;
        this.publisher = publisherResponse;
        this.users = users;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PublisherResponse getPublisher() {
        return publisher;
    }

    public void setPublisher(PublisherResponse publisherResponse) {

        this.publisher = publisherResponse;
    }

    public List<UserResponse> getUsers() {
        return users;
    }

    public void setUsers(List<UserResponse> users) {
        this.users = users;
    }
}
