package org.example.restservlet.service;

import org.example.restservlet.entity.User;

public interface UserService extends CRUDService<User>{

    User addGame(Long userId, Long gameId);

    User deleteGame(Long userId, Long gameId);

}
