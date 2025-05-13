package com.boardcamp.api.exceptions.game_exceptions;

public class GameStockException extends RuntimeException {
     public GameStockException (String message){
        super(message);
    }
}
