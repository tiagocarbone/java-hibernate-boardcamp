package com.boardcamp.api.models;


import com.boardcamp.api.dtos.GameDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "games")
public class GameModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 150, nullable = false)
    private String name;

    @Column(length = 300)
    private String image;

    @Column(nullable = false)
    private int stockTotal;

    @Column(nullable = false)
    private int pricePerDay;



    public GameModel(GameDTO body){
        this.name = body.getName();
        this.image = body.getImage();
        this.stockTotal = body.getStockTotal();
        this.pricePerDay = body.getPricePerDay();
    }
}
