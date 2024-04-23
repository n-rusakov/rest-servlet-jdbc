package org.example.restservlet.web.dto;

import java.util.List;

public class UserGamesResponse {
    private Long id;

    private String name;

    private String email;

    private List<GameResponse> games;

    public UserGamesResponse() {
    }

    public UserGamesResponse(Long id, String name, String email, List<GameResponse> games) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.games = games;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<GameResponse> getGames() {
        return games;
    }

    public void setGames(List<GameResponse> games) {
        this.games = games;
    }
}


