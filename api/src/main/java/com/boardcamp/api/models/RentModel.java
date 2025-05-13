package com.boardcamp.api.models;

import java.time.LocalDate;

import com.boardcamp.api.dtos.RentDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rents")
public class RentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customerId")
    private CustomerModel customer;

    @ManyToOne
    @JoinColumn(name = "gameId")
    private GameModel game;

    @Column(nullable = false)
    private LocalDate rentDate;

    @Column(nullable = false)
    private int daysRented;

    @Column
    private LocalDate returnDate;

    @Column(nullable = false)
    private int originalPrice;

    @Column
    private int delayFee;

    public RentModel(RentDTO body) {
        this.customer = new CustomerModel();
        this.customer.setId(body.getCustomerId());

        this.game = new GameModel();
        this.game.setId(body.getGameId());

        this.daysRented = body.getDaysRented();
        this.rentDate = LocalDate.now();
        this.delayFee = 0;
    }

}
