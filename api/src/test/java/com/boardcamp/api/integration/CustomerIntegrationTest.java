package com.boardcamp.api.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.repositories.RentRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
// @EnableJpaRepositories("com.boardcamp.api.repositories")
class CustomerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private RentRepository rentRepository;

    @BeforeEach
    @AfterEach
    void cleanUpDatabase() {
        rentRepository.deleteAll();
        customerRepository.deleteAll();
        gameRepository.deleteAll();
    }

    @Test
    void givenCustomerWithWrongPhone_whenCreatingCustomer_thenThrowsError() {

        CustomerDTO dto = new CustomerDTO("Tiago", "abc1234567", "12345678901");
        HttpEntity<CustomerDTO> body = new HttpEntity<>(dto);

        ResponseEntity<String> response = restTemplate.exchange(
                "/customers",
                HttpMethod.POST,
                body,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("O telefone deve conter apenas números"));

    }

    @Test
    void givenCustomerWithWrongCpf_whenCreatingCustomer_thenThrowsError() {

        CustomerDTO dto = new CustomerDTO("Tiago", "1234567889", "a2345678901");
        HttpEntity<CustomerDTO> body = new HttpEntity<>(dto);

        ResponseEntity<String> response = restTemplate.exchange(
                "/customers",
                HttpMethod.POST,
                body,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("O CPF deve conter apenas números"));
       
    }

    @Test
    void givenCustomerAlreadyIncluded_whenCreatingCustomer_thenThrowsError() {

        CustomerDTO dto = new CustomerDTO("Tiago", "1234567889", "12345678901");

        HttpEntity<CustomerDTO> body1 = new HttpEntity<>(dto);

        ResponseEntity<String> response1 = restTemplate.exchange(
                "/customers",
                HttpMethod.POST,
                body1,
                String.class);

        HttpEntity<CustomerDTO> body2 = new HttpEntity<>(dto);

        ResponseEntity<String> response2 = restTemplate.exchange(
                "/customers",
                HttpMethod.POST,
                body2,
                String.class);

        assertEquals(HttpStatus.CONFLICT, response2.getStatusCode());
        assertTrue(response2.getBody().contains("A customer with this cpf already exists"));
        

    }

    @Test
    void givenCustomerAlreadyIncluded_whenCreatingCustomer_thenCreatesCustomer() {
        CustomerDTO dto = new CustomerDTO("Tiago", "1234567889", "12345678901");
        HttpEntity<CustomerDTO> body = new HttpEntity<>(dto);

        ResponseEntity<String> response = restTemplate.exchange(
                "/customers",
                HttpMethod.POST,
                body,
                String.class);

        CustomerModel saved = customerRepository.findAll().get(0);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(dto.getName(), saved.getName());
        assertEquals(dto.getPhone(), saved.getPhone());
        assertEquals(dto.getCpf(), saved.getCpf());
    }

    @Test
    void givenCustomers_whenGettingUsers_thenGetsTheCustomers() {

        CustomerDTO dto = new CustomerDTO("Tiago", "1234567889", "12345678901");
        CustomerDTO dto2 = new CustomerDTO("Tiago", "1234567889", "12345678900");

        HttpEntity<CustomerDTO> body = new HttpEntity<>(dto);

        ResponseEntity<String> response = restTemplate.exchange(
                "/customers",
                HttpMethod.POST,
                body,
                String.class);

        HttpEntity<CustomerDTO> body2 = new HttpEntity<>(dto2);

        ResponseEntity<String> response2 = restTemplate.exchange(
                "/customers",
                HttpMethod.POST,
                body2,
                String.class);

        ResponseEntity<List<CustomerModel>> getResponse = restTemplate.exchange(
                "/customers",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CustomerModel>>() {
                });

        List<CustomerModel> customers = customerRepository.findAll();

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(customers.size(), 2);

    }

    @Test
    void givenCustomerId_whenGettingCustomer_thenReturnsCustomer() {

        CustomerDTO dto = new CustomerDTO("Tiago", "1234567889", "12345678901");
        HttpEntity<CustomerDTO> body = new HttpEntity<>(dto);

        ResponseEntity<String> postResponse = restTemplate.exchange(
                "/customers",
                HttpMethod.POST,
                body,
                String.class);

        CustomerModel saved = customerRepository.findAll().get(0);

        ResponseEntity<CustomerModel> getResponse = restTemplate.exchange(
                "/customers/" + saved.getId(),
                HttpMethod.GET,
                null,
                CustomerModel.class);

        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        CustomerModel returned = getResponse.getBody();
        assertNotNull(returned);
        assertEquals(dto.getName(), returned.getName());
        assertEquals(dto.getPhone(), returned.getPhone());
        assertEquals(dto.getCpf(), returned.getCpf());
    }

    @Test
    void givenWrongCustomerId_whenGettingCustomer_thenThrowsError() {
        CustomerDTO dto = new CustomerDTO("Tiago", "1234567889", "12345678901");
        HttpEntity<CustomerDTO> body = new HttpEntity<>(dto);

        ResponseEntity<String> postResponse = restTemplate.exchange(
                "/customers",
                HttpMethod.POST,
                body,
                String.class);

        CustomerModel saved = customerRepository.findAll().get(0);

        ResponseEntity<String> getResponse = restTemplate.exchange(
                "/customers/" + (saved.getId() + 1),
                HttpMethod.GET,
                null,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
        assertTrue(getResponse.getBody().contains("Could not find user with this id"));

    }


}
