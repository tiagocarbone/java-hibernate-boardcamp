package com.boardcamp.api.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RentDTO {
 
  @NotNull  
  private Long customerId;
  
  @NotNull
  private Long gameId;
  
  @NotNull
  @Min(value = 1, message = "O valor daysRented deve ser maior que zero")
  private int daysRented;


}
