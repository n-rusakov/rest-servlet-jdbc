package org.example.restservlet.entity;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private Long id;

    private String title;

    private Publisher publisher;

    List<User> users = new ArrayList<>();

    public Game() {
    }

    public Game(Long id, String title, Publisher publisher) {
        this.id = id;
        this.title = title;
        this.publisher = publisher;
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

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
