package org.example.restservlet.web.dto;

public class GameResponse {

    private Long id;

    private String title;

    private PublisherResponse publisher;

    public GameResponse() {
    }

    public GameResponse(Long id, String title,
                        PublisherResponse publisherResponse) {
        this.id = id;
        this.title = title;
        this.publisher = publisherResponse;
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

    public void setPublisher(PublisherResponse publisherResponse)
    {
        this.publisher = publisherResponse;
    }
}
