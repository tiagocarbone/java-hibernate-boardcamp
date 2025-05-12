package com.boardcamp.api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.services.GameService;


import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@RequestMapping("/games")
public class GameController {
    
    private final GameService gameService;

    public GameController(GameService gameService){
        this.gameService = gameService;
    }

    @GetMapping("")
    public String getGames() {
        return "ffc";
    }

    @PostMapping("")
    public ResponseEntity<GameModel> createGame(@RequestBody @Valid GameDTO body) {

        GameModel game = gameService.createGame(body);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(game);
    }
    
    

}
