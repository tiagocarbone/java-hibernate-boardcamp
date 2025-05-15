package com.boardcamp.api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.test.context.SpringBootTest;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.exceptions.customer_exceptions.CustomerConflictException;
import com.boardcamp.api.exceptions.customer_exceptions.CustomerInvalidCpfException;
import com.boardcamp.api.exceptions.customer_exceptions.CustomerInvalidPhoneException;
import com.boardcamp.api.exceptions.customer_exceptions.CustomerNotFoundException;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.services.CustomerService;

@SpringBootTest
class CustomerServiceUnitTests {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

   
    @Test
    void givenCustomerWithWrongPhone_whenCreatingUser_thenThrowsError() {
        String phone = "testandotelefone";

        CustomerDTO customer = new CustomerDTO("Tiago", phone, "12345678901");

        doReturn(false).when(customerRepository).existsByCpf(any());

        CustomerInvalidPhoneException exception = assertThrows(CustomerInvalidPhoneException.class,
                () -> customerService.createCustomer(customer));

        verify(customerRepository, times(0)).save(any());
        verify(customerRepository, times(0)).existsByCpf(any());

        assertNotNull(exception);
        assertEquals("O telefone deve conter apenas números", exception.getMessage());

    }

    @Test
    void givenCustomerWithWrongCPF_whenCreatingUser_thenThrowsError() {

        String cpf = "testandocpf";

        CustomerDTO customer = new CustomerDTO("Tiago", "1234567843", cpf);

        doReturn(false).when(customerRepository).existsByCpf(any());

        CustomerInvalidCpfException exception = assertThrows((CustomerInvalidCpfException.class),
                () -> customerService.createCustomer(customer));

        verify(customerRepository, times(0)).save(any());
        verify(customerRepository, times(0)).existsByCpf(any());

        assertNotNull(exception);
        assertEquals("O CPF deve conter apenas números", exception.getMessage());

    }

    @Test
    void givenCostumerAlreadyIncluded_whenCreatingUser_thenThrowsError() {

        CustomerDTO customer = new CustomerDTO("Tiago", "1234567843", "85777777777");

        doReturn(true).when(customerRepository).existsByCpf(any());

        CustomerConflictException exception = assertThrows(CustomerConflictException.class,
                () -> customerService.createCustomer(customer));

        verify(customerRepository, times(0)).save(any());
        verify(customerRepository, times(1)).existsByCpf(any());
        assertNotNull(exception);
        assertEquals("A customer with this cpf already exists", exception.getMessage());

    }

    @Test
    void givenCustomer_whenCreatingUser_thenCreatesUser() {
        CustomerDTO customer = new CustomerDTO("Tiago", "1234567843", "85777777777");

        when(customerRepository.existsByCpf(any())).thenReturn(false);

        CustomerModel savedCustomer = new CustomerModel(1L, "Tiago", "1234567843", "85777777777");
        when(customerRepository.save(any())).thenReturn(savedCustomer);

        CustomerModel result = customerService.createCustomer(customer);

        verify(customerRepository, times(1)).existsByCpf("85777777777");
        verify(customerRepository, times(1)).save(any());

        assertNotNull(result);
        assertEquals("Tiago", result.getName());
        assertEquals("1234567843", result.getPhone());
        assertEquals("85777777777", result.getCpf());
    }



    @Test
    void givenCustomers_whenGettingAll_thenReturnListOfCustomer() {
        CustomerModel savedCustomer1 = new CustomerModel(1L, "Tiago", "1234567843", "85777777777");
        CustomerModel savedCustomer2 = new CustomerModel(2L, "Carlos", "1234567890", "09876543219");
        List<CustomerModel> customers = List.of(savedCustomer1, savedCustomer2);

        when(customerRepository.findAll()).thenReturn(customers);

        List<CustomerModel> result = customerService.getCustomers();

        verify(customerRepository, times(1)).findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Tiago", result.get(0).getName());
        assertEquals("Carlos", result.get(1).getName());
    }

 

    @Test
    void givenWrongId_whenGettingById_thenThrowsError() {
        Long invalidId = 999L;

        when(customerRepository.findById(invalidId)).thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getById(invalidId));

        assertEquals("Could not find user with this id", exception.getMessage());

        verify(customerRepository, times(1)).findById(invalidId);
    }


    @Test
    void givenId_whenGettingById_thenReturnCustomer(){

        CustomerModel savedCustomer1 = new CustomerModel(1L, "Tiago", "1234567843", "85777777777");

        when(customerRepository.findById(any())).thenReturn(Optional.of(savedCustomer1));

        CustomerModel calledCustomer = customerService.getById(1L);

        assertEquals(savedCustomer1, calledCustomer);
        assertNotNull(calledCustomer);
        verify(customerRepository, times(1)).findById(1L);

    }
}
