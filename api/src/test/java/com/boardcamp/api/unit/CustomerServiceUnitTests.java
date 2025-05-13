package com.boardcamp.api.unit;

import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

import com.boardcamp.api.services.CustomerService;

@SpringBootTest
public class CustomerServiceUnitTests {
    
    @InjectMocks
    private CustomerService customerService;
}
