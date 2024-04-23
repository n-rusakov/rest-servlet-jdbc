package org.example.restservlet.service.impl;

import org.example.restservlet.entity.User;
import org.example.restservlet.exception.EntityNotFoundException;
import org.example.restservlet.repository.UserRepository;
import org.example.restservlet.service.UserService;

import java.text.MessageFormat;
import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {

        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException((MessageFormat.format(
                        "User с id {0} не найден", id))));
    }

    @Override
    public User insert(User user) {
        return userRepository.insert(user);
    }

    @Override
    public User update(Long id, User user) {
        User existed = findById(id);
        existed.setName(user.getName());
        existed.setEmail(user.getEmail());

        return userRepository.update(existed);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User addGame(Long userId, Long gameId) {
        try {
            userRepository.addSubscription(userId, gameId);
        } catch (RuntimeException e) {
            throw new EntityNotFoundException("Incorrect user_id or game_id");
        }

        return findById(userId);
    }

    @Override
    public User deleteGame(Long userId, Long gameId) {
        userRepository.deleteSubscription(userId, gameId);

        return findById(userId);
    }
}
