package com.boardcamp.api.dtos;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameDTO {


    @NotBlank
    @Size(max = 150)
    private String name;


    private String image;

    @NotNull
    @Min(value = 1, message = "O valor stockTotal deve ser maior que zero")
    private int stockTotal;

    @NotNull
    @Min(value = 1, message = "O valor pricePerDay deve ser maior que zero")
    private int pricePerDay;


}
