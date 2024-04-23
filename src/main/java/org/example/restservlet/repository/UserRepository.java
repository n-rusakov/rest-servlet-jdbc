package org.example.restservlet.repository;

import org.example.restservlet.entity.User;

public interface UserRepository extends CRUDRepository<User, Long> {
    void addSubscription(Long userId, Long gameId);

    void deleteSubscription(Long userId, Long gameId);
}
