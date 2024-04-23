package org.example.restservlet.web.dto;

public class PublisherUpsertRequest {

    String name;

    public PublisherUpsertRequest() {
    }

    public PublisherUpsertRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
