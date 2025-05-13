package com.boardcamp.api.exceptions.rent_exceptions;

public class RentClosedException extends RuntimeException {
    public RentClosedException (String message){
        super(message);
    }
}
