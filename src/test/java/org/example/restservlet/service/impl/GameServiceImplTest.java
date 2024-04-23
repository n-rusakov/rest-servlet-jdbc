package org.example.restservlet.service.impl;

import org.example.restservlet.entity.Game;
import org.example.restservlet.entity.Publisher;
import org.example.restservlet.exception.EntityNotFoundException;
import org.example.restservlet.repository.GameRepository;
import org.example.restservlet.service.GameService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

public class GameServiceImplTest {

    private GameRepository gameRepository = Mockito.mock(GameRepository.class);

    private GameService gameService = new GameServiceImpl(gameRepository);

    @Test
    public void whenGetAll_thenCorrectCount() {
        List<Game> expected = List.of(
                new Game(1L, "G1", new Publisher(1l, "P1")),
                new Game(2L, "G2", new Publisher(1l, "P1")),
                new Game(3L, "G3", new Publisher(2l, "P2")));

        Mockito.when(gameRepository.findAll()).thenReturn(expected);

        List<Game> actual = gameService.findAll();

        Assert.assertEquals(expected, actual);
        Mockito.verify(gameRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void whenGetById_thenUsedRepository() {
        Game expected = new Game(5L, "test", new Publisher(1L, "P1"));
        Mockito.when(gameRepository.findById(5L)).thenReturn(Optional.of(expected));

        Game actual = gameService.findById(5L);

        Assert.assertEquals(expected, actual);
        Mockito.verify(gameRepository, Mockito.times(1)).findById(5L);
    }

    @Test
    public void whenGetNotExistedId_thenThrow(){
        Mockito.when(gameRepository.findById(5L)).thenReturn(Optional.empty());

        Assert.assertThrows(EntityNotFoundException.class, () -> gameService.findById(5L));
    }

    @Test
    public void whenInsert_thenUsedRepository() {
        Game inserted = new Game();
        inserted.setTitle("test");
        inserted.setPublisher(new Publisher(1L, "P1"));

        Game expected = new Game(1L, "test", new Publisher(1L, "P1"));
        Mockito.when(gameRepository.insert(inserted)).thenReturn(expected);

        Game actual = gameService.insert(inserted);

        Assert.assertEquals(expected, actual);
        Mockito.verify(gameRepository, Mockito.times(1)).insert(Mockito.any());
    }

    @Test
    public void whenUpdate_thenUseRepository() {
        Game updated = new Game(2L, "testtest", new Publisher(1L, "P1"));
        Game expected = new Game(2L, "testtest", new Publisher(1L, "P1"));

        Mockito.when(gameRepository.findById(2L)).thenReturn(Optional.of(updated));
        Mockito.when(gameRepository.update(updated)).thenReturn(expected);

        Game actual = gameService.update(2L, updated);

        Assert.assertEquals(expected, actual);
        Mockito.verify(gameRepository, Mockito.times(1)).update(Mockito.any());
        Mockito.verify(gameRepository, Mockito.times(1)).findById(2L);
    }

    @Test
    public void whenDelete_thenUserRepository() {

        gameService.deleteById(2L);
        Mockito.verify(gameRepository, Mockito.times(1)).deleteById(2L);
    }
}
