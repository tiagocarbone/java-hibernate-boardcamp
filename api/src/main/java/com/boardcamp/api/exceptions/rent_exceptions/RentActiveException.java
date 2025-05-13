package com.boardcamp.api.exceptions.rent_exceptions;

public class RentActiveException extends RuntimeException {
    public RentActiveException (String message){
        super(message);
    }
}
