package com.boardcamp.api.exceptions.game_exceptions;

public class GameNameConflictException extends RuntimeException{
    public GameNameConflictException (String message){
        super(message);
    }
    
}
