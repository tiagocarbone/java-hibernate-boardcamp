package com.boardcamp.api.exceptions.customer_exceptions;

public class CustomerInvalidPhoneException extends RuntimeException {
    public CustomerInvalidPhoneException (String message) {
        super(message);
    }
}
