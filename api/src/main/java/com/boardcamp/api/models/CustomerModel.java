package com.boardcamp.api.models;

import com.boardcamp.api.dtos.CustomerDTO;

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
@Table(name = "customers")
public class CustomerModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 150, nullable = false)
    private String name;

    @Column(length = 30, nullable = false)
    private String phone;

    @Column(length = 11, nullable = false)
    private String cpf;

    

    public CustomerModel(CustomerDTO body){
        this.name = body.getName();
        this.cpf = body.getCpf();
        this.phone = body.getPhone();
    }
    
}
