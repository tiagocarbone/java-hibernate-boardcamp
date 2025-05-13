package com.boardcamp.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.boardcamp.api.exceptions.customer_exceptions.CustomerConflictException;
import com.boardcamp.api.exceptions.customer_exceptions.CustomerInvalidCpfException;
import com.boardcamp.api.exceptions.customer_exceptions.CustomerInvalidPhoneException;
import com.boardcamp.api.exceptions.customer_exceptions.CustomerNotFoundException;
import com.boardcamp.api.exceptions.game_exceptions.GameNameConflictException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ GameNameConflictException.class })
    public ResponseEntity<String> handleGameNameConflict(GameNameConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler({ CustomerConflictException.class })
    public ResponseEntity<String> handleCustomerCpfConflict(CustomerConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler({ CustomerInvalidCpfException.class })
    public ResponseEntity<String> handleCustomerInvalidCpfException(CustomerInvalidCpfException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler({ CustomerInvalidPhoneException.class })
    public ResponseEntity<String> handleCustomerInvalidPhoneException(CustomerInvalidPhoneException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler({CustomerNotFoundException.class})
    public ResponseEntity<String> handleCustomerNotFoundException(CustomerNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

}
