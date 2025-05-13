package com.boardcamp.api.exceptions.game_exceptions;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException (String message){
        super(message);
    }
}
