package com.boardcamp.api.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.repositories.GameRepository;


@Service
public class GameService {
    
    final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public GameModel createGame(GameDTO body){

        GameModel game = new GameModel(body);

        return gameRepository.save(game);
    }



}
