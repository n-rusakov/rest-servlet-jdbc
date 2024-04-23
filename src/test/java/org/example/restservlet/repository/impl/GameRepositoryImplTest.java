package org.example.restservlet.repository.impl;

import org.example.restservlet.AbstractTest;
import org.example.restservlet.entity.Game;
import org.example.restservlet.entity.Publisher;
import org.example.restservlet.repository.GameRepository;
import org.example.restservlet.repository.PublisherRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.ext.ScriptUtils;

import java.util.Optional;

class GameRepositoryImplTest extends AbstractTest {

    private final GameRepository gameRepository =
            new GameRepositoryImpl(dataSource);

    private final PublisherRepository publisherRepository =
            new PublisherRepositoryImpl(dataSource);

    @BeforeEach
    public void dropCreateSchema() {
        ScriptUtils.runInitScript(jdbcDelegate, "db/drop_create_schema.sql");
        ScriptUtils.runInitScript(jdbcDelegate, "db/fill_database.sql");
    }

    @Test
    void whenGetAll_thenCorrectCount() {
        int count = gameRepository.findAll().size();

        Assert.assertEquals(4, count);
    }

    @Test
    void whenGetById_thenCorrectElement() {
        Game game = gameRepository.findById(3L).get();

        Assert.assertEquals("Tanki", game.getTitle());
        Assert.assertEquals("SONY", game.getPublisher().getName());
    }

    @Test
    void whenGetIncorrectId_thenOptionalEmpty() {
        Optional<Game> game = gameRepository.findById(10L);
        Assert.assertTrue(game.isEmpty());
    }

    @Test
    void whenInsert_thenCorrectAdded() {
        Publisher publisher = publisherRepository.findById(2L).get();
        Game game = new Game();
        game.setTitle("TEST");
        game.setPublisher(publisher);

        Game newGame = gameRepository.insert(game);
        Assert.assertEquals(Long.valueOf(5L),  newGame.getId());
        Assert.assertEquals(Long.valueOf(2L), newGame.getPublisher().getId());

        Game existed = gameRepository.findById(5L).get();
        Assert.assertEquals("TEST", existed.getTitle());
        Assert.assertEquals(Long.valueOf(2L), existed.getPublisher().getId());
    }

    @Test
    void whenUpdate_thenCorrectData() {
        Game game = gameRepository.findById(2L).get();
        game.setTitle("TEST");

        gameRepository.update(game);

        Game existed = gameRepository.findById(2L).get();
        Assert.assertEquals("TEST", existed.getTitle());
    }

    @Test
    void whenDelete_thenDeletedAndCorrectCount() {
        gameRepository.deleteById(2L);

        Optional<Game> game = gameRepository.findById(2L);
        Assert.assertEquals(Optional.empty(), game);

        int count = gameRepository.findAll().size();
        Assert.assertEquals(3, count);
    }
}
