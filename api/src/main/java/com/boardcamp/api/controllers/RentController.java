package com.boardcamp.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.boardcamp.api.dtos.RentDTO;
import com.boardcamp.api.models.RentModel;
import com.boardcamp.api.services.RentService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/rentals")
public class RentController {
    
    final RentService rentService;

    public RentController(RentService rentService){
        this.rentService = rentService;
    }


    @GetMapping("")
    public ResponseEntity<List<RentModel>> getRents(){

        List<RentModel> rents = rentService.getAllRents();

        return ResponseEntity.status(HttpStatus.OK).body(rents);
        
    }

    @PostMapping("")
    public ResponseEntity<RentModel> createRental(@RequestBody @Valid RentDTO body) {

        RentModel rent = rentService.createRental(body);

        return ResponseEntity.status(HttpStatus.OK).body(rent);

    }

    @PostMapping("/{id}/return")
    public ResponseEntity<RentModel> closeRental(@PathVariable("id")  Long id) {

        RentModel rent = rentService.closeRental(id);
        return ResponseEntity.status(HttpStatus.OK).body(rent);

    }


    @DeleteMapping("/{id}")
     public ResponseEntity<Object> deleteRental(@PathVariable("id")  Long id) {

        rentService.deleteRental(id);
        return ResponseEntity.status(HttpStatus.OK).build();

    }
}
