package com.boardcamp.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerDTO {
    
    @NotBlank
    private String name;

    @NotBlank
    @Size(min = 10, max = 11, message = "O telefone deve ter entre 10 e 11 dígitos.")
    private String phone;

    @Size(min = 11, max = 11, message = "O CPF deve ter 11 dígitos.")
    @NotBlank
    private String cpf;

}
