package org.example.restservlet.repository.impl;

import org.example.restservlet.AbstractTest;
import org.example.restservlet.entity.User;
import org.example.restservlet.repository.UserRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.ext.ScriptUtils;

import java.util.Optional;

class UserRepositoryImplTest extends AbstractTest {

    private final UserRepository userRepository =
            new UserRepositoryImpl(dataSource);

    @BeforeEach
    public void dropCreateSchema() {
        ScriptUtils.runInitScript(jdbcDelegate, "db/drop_create_schema.sql");
        ScriptUtils.runInitScript(jdbcDelegate, "db/fill_database.sql");
    }

    @Test
    void whenGetAll_thenCorrectCount() {
        int count = userRepository.findAll().size();

        Assertions.assertEquals(4, count);
    }

    @Test
    void whenGetById_thenCorrectElement() {
        User user = userRepository.findById(4L).get();

        Assert.assertEquals("Valentina", user.getName());
        Assert.assertEquals("valyusha@plyusha.net", user.getEmail());
    }

    @Test
    void whenGetIncorrectId_thenOptionalEmpty() {
        Optional<User> user = userRepository.findById(5L);
        Assert.assertTrue(user.isEmpty());
    }

    @Test
    void whenInsert_thenCorrectAdded() {
        User user = new User();
        user.setName("TEST_NAME");
        user.setEmail("TEST_EMAIL");

        User newUser = userRepository.insert(user);
        Assert.assertEquals(Long.valueOf(5L),  newUser.getId());

        User existed = userRepository.findById(5L).get();
        Assert.assertEquals("TEST_NAME", existed.getName());
        Assert.assertEquals("TEST_EMAIL", existed.getEmail());

    }

    @Test
    void whenUpdate_thenCorrectData() {
        User user = userRepository.findById(2L).get();
        user.setName("TEST_NAME");
        user.setEmail("TEST_EMAIL");

        userRepository.update(user);

        User existed = userRepository.findById(2L).get();
        Assert.assertEquals("TEST_NAME", existed.getName());
        Assert.assertEquals("TEST_EMAIL", existed.getEmail());
    }

    @Test
    void whenDelete_thenDeletedAndCorrectCount() {
        userRepository.deleteById(3L);

        Optional<User> user = userRepository.findById(3L);
        Assert.assertEquals(Optional.empty(), user);

        int count = userRepository.findAll().size();
        Assert.assertEquals(3, count);
    }

    @Test
    void whenAddSubscription_thenCorrectCount(){
        userRepository.addSubscription(4L, 3L);

        User user = userRepository.findById(4L).get();

        Assert.assertEquals(1, user.getGames().size());
        Assert.assertEquals(Long.valueOf(3L), user.getGames().get(0).getId());
    }

    @Test
    void whenDeleteSubscription_thenCorrectCount() {
        userRepository.deleteSubscription(1L, 2L);

        User user = userRepository.findById(1L).get();

        Assert.assertEquals(1, user.getGames().size());

    }

}
