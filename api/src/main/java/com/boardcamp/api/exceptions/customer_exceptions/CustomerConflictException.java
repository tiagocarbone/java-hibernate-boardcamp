package com.boardcamp.api.exceptions.customer_exceptions;

public class CustomerConflictException extends RuntimeException {
    public CustomerConflictException (String message){
        super(message);
    }
    
}
