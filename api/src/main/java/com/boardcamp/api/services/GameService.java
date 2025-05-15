package com.boardcamp.api.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.exceptions.game_exceptions.*;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.repositories.GameRepository;

@Service
public class GameService {

    final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<GameModel> getGames() {
        return gameRepository.findAll();
    }

    public GameModel createGame(GameDTO body) {

        if (gameRepository.existsByName(body.getName())) {
            throw new GameNameConflictException("A game with this name already exists");
        }

        GameModel game = new GameModel(body);

        return gameRepository.save(game);
    }

}
