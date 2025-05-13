package com.boardcamp.api.exceptions.customer_exceptions;

public class CustomerNotFoundException  extends RuntimeException{
    public CustomerNotFoundException (String message) {
        super(message);
    }
    
}
