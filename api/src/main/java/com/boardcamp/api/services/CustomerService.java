package com.boardcamp.api.services;

import java.util.List;

import org.springframework.stereotype.Service;
import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.exceptions.customer_exceptions.CustomerConflictException;
import com.boardcamp.api.exceptions.customer_exceptions.CustomerInvalidCpfException;
import com.boardcamp.api.exceptions.customer_exceptions.CustomerInvalidPhoneException;
import com.boardcamp.api.exceptions.customer_exceptions.CustomerNotFoundException;
import com.boardcamp.api.models.CustomerModel;

import com.boardcamp.api.repositories.CustomerRepository;

@Service
public class CustomerService {

    final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerModel> getCustomers() {
        return customerRepository.findAll();
    }

    public CustomerModel getById(Long id) {

        return customerRepository
                .findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Could not find user with this id"));

    }

    public CustomerModel createCustomer(CustomerDTO body) {

        try {
            Long.parseLong(body.getPhone());
        } catch (NumberFormatException e) {
            throw new CustomerInvalidPhoneException("O telefone deve conter apenas números");
        }

        try {
            Long.parseLong(body.getCpf());
        } catch (NumberFormatException e) {
            throw new CustomerInvalidCpfException("O CPF deve conter apenas números");
        }

        if (customerRepository.existsByCpf(body.getCpf())) {
            throw new CustomerConflictException("A customer with this cpf already exists");
        }

        CustomerModel customer = new CustomerModel(body);

        return customerRepository.save(customer);
    }

}
