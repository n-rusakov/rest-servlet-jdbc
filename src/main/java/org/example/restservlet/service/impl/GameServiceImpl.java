package org.example.restservlet.service.impl;

import org.example.restservlet.entity.Game;
import org.example.restservlet.exception.EntityNotFoundException;
import org.example.restservlet.repository.GameRepository;
import org.example.restservlet.service.GameService;

import java.text.MessageFormat;
import java.util.List;

public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;

    public GameServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public List<Game> findAll() {
        return gameRepository.findAll();
    }

    @Override
    public Game findById(Long id) {
        return gameRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException((MessageFormat.format(
                        "Game с id {0} не найдена", id))));
    }

    @Override
    public Game insert(Game game) {
        return gameRepository.insert(game);
    }

    @Override
    public Game update(Long id, Game game) {
        Game existed = findById(id);

        existed.setTitle(game.getTitle());
        existed.setPublisher(game.getPublisher());

        return gameRepository.update(existed);
    }

    @Override
    public void deleteById(Long id) {
        gameRepository.deleteById(id);
    }
}
