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
import com.boardcamp.api.exceptions.game_exceptions.GameNotFoundException;
import com.boardcamp.api.exceptions.game_exceptions.GameStockException;
import com.boardcamp.api.exceptions.rent_exceptions.RentActiveException;
import com.boardcamp.api.exceptions.rent_exceptions.RentClosedException;
import com.boardcamp.api.exceptions.rent_exceptions.RentNotFoundException;

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

    @ExceptionHandler({GameNotFoundException.class})
    public ResponseEntity<String> handleGameNotFoundException(GameNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler({GameStockException.class})
    public ResponseEntity<String> handleGameStockException(GameStockException exception){
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(exception.getMessage());
    }

    @ExceptionHandler({RentNotFoundException.class})
    public ResponseEntity<String> handleRentNotFoundException(RentNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }


    @ExceptionHandler({RentClosedException.class})
    public ResponseEntity<String> handleRentClosedException(RentClosedException exception){
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(exception.getMessage());
    }

    @ExceptionHandler({RentActiveException.class})
    public ResponseEntity<String> handleRentActiveException(RentActiveException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

}
