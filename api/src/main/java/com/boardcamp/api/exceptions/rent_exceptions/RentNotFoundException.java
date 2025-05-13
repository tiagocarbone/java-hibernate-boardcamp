package com.boardcamp.api.exceptions.rent_exceptions;

public class RentNotFoundException extends RuntimeException {
    public RentNotFoundException (String message){
        super(message);
    }
}
