package com.boardcamp.api.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.print.DocFlavor.STRING;

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

import com.boardcamp.api.dtos.RentDTO;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.models.RentModel;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.repositories.RentRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RentIntegrationTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    @AfterEach
    void cleanUpDatabase() {
        rentRepository.deleteAll();
        customerRepository.deleteAll();
        gameRepository.deleteAll();
    }

    @Test
    void givenRents_whenGettingAllRents_thenReturnRents() {

        CustomerModel customer = new CustomerModel(null, "Tiago", "1234567890", "12345678901");
        customer = customerRepository.save(customer);

        GameModel game = new GameModel(null, "Jogo Teste", "imagem", 2, 3000);
        game = gameRepository.save(game);

        RentDTO dto1 = new RentDTO(customer.getId(), game.getId(), 3);
        RentDTO dto2 = new RentDTO(customer.getId(), game.getId(), 3);

        HttpEntity<RentDTO> body1 = new HttpEntity<>(dto1);
        HttpEntity<RentDTO> body2 = new HttpEntity<>(dto2);

        ResponseEntity<String> postResponse1 = restTemplate.exchange(
                "/rentals",
                HttpMethod.POST,
                body1,
                String.class);

        ResponseEntity<String> postResponse2 = restTemplate.exchange(
                "/rentals",
                HttpMethod.POST,
                body2,
                String.class);

        ResponseEntity<List<RentModel>> getResponse = restTemplate.exchange(
                "/rentals",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<RentModel>>() {
                });

        List<RentModel> rents = getResponse.getBody();

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(rents);
        assertEquals(2, rents.size());
    }

    @Test
    void givenNonExistingGame_whenCreatingRent_thenThrowsError() {

        CustomerModel customer = new CustomerModel(null, "Tiago", "1234567890", "12345678901");
        customer = customerRepository.save(customer);

        RentDTO dto1 = new RentDTO(customer.getId(), 3L, 3);

        HttpEntity<RentDTO> body1 = new HttpEntity<>(dto1);

        ResponseEntity<String> postResponse1 = restTemplate.exchange(
                "/rentals",
                HttpMethod.POST,
                body1,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, postResponse1.getStatusCode());
        assertTrue(postResponse1.getBody().contains("a game with this id does not exist"));

    }

    @Test
    void givenNonExistingCustomer_whenCreatingRent_thenThrowsError() {

        GameModel game = new GameModel(null, "jogo", "imagem", 3, 3000);
        game = gameRepository.save(game);

        RentDTO dto1 = new RentDTO(1L, game.getId(), 3);

        HttpEntity<RentDTO> body1 = new HttpEntity<>(dto1);

        ResponseEntity<String> postResponse1 = restTemplate.exchange(
                "/rentals",
                HttpMethod.POST,
                body1,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, postResponse1.getStatusCode());
        assertTrue(postResponse1.getBody().contains("a customer with this id does not exist"));

    }

    @Test
    void givenGameOutOfStock_whenCreatingRent_ThenThrowsError() {
        CustomerModel customer = new CustomerModel(null, "Tiago", "1234567890", "12345678901");
        customer = customerRepository.save(customer);

        GameModel game = new GameModel(null, "Jogo Teste", "imagem", 0, 3000);
        game = gameRepository.save(game);

        RentDTO dto1 = new RentDTO(customer.getId(), game.getId(), 3);

        HttpEntity<RentDTO> body1 = new HttpEntity<>(dto1);

        ResponseEntity<String> postResponse1 = restTemplate.exchange(
                "/rentals",
                HttpMethod.POST,
                body1,
                String.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, postResponse1.getStatusCode());
        assertTrue(postResponse1.getBody().contains("this game has no stock to rent"));

    }

    @Test
    void givenGameAndCustomer_whenCreatingRent_ThenCreatesRent() {
        CustomerModel customer = new CustomerModel(null, "Tiago", "1234567890", "12345678901");
        customer = customerRepository.save(customer);

        GameModel game = new GameModel(null, "Jogo Teste", "imagem", 3, 3000);
        game = gameRepository.save(game);

        RentDTO dto1 = new RentDTO(customer.getId(), game.getId(), 3);
        HttpEntity<RentDTO> body1 = new HttpEntity<>(dto1);

        ResponseEntity<RentModel> postResponse1 = restTemplate.exchange(
                "/rentals",
                HttpMethod.POST,
                body1,
                RentModel.class);

        RentModel rent = postResponse1.getBody();
        Optional<RentModel> savedRent = rentRepository.findById(rent.getId());

        assertEquals(HttpStatus.OK, postResponse1.getStatusCode());
        assertEquals(rent, savedRent.get());

    }

    @Test
    void givenNonExistingRent_whenClosingRental_ThenThrowError() {

        CustomerModel customer = new CustomerModel(null, "Tiago", "1234567890", "12345678901");
        customer = customerRepository.save(customer);

        GameModel game = new GameModel(null, "Jogo Teste", "imagem", 3, 3000);
        game = gameRepository.save(game);

        RentModel rent = new RentModel(null, customer, game, LocalDate.now(), 3, null, 3000, 0);

        RentModel saved = rentRepository.save(rent);
        Long wrongId = saved.getId() + 1000;

        ResponseEntity<String> postResponse = restTemplate.exchange(
                "/rentals/" + wrongId + "/return",
                HttpMethod.POST,
                null,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, postResponse.getStatusCode());
        assertTrue(postResponse.getBody().contains("a rent with this id does not exist"));
    }

    @Test
    void givenClosedRent_whenClosingRental_ThenThrowError() {
        CustomerModel customer = new CustomerModel(null, "Tiago", "1234567890", "12345678901");
        customer = customerRepository.save(customer);

        GameModel game = new GameModel(null, "Jogo Teste", "imagem", 3, 3000);
        game = gameRepository.save(game);

        RentModel rent = new RentModel(null, customer, game, LocalDate.now(), 3, LocalDate.now().plusDays(3), 3000, 0);

        RentModel saved = rentRepository.save(rent);

        ResponseEntity<String> postResponse = restTemplate.exchange(
                "/rentals/" + saved.getId() + "/return",
                HttpMethod.POST,
                null,
                String.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, postResponse.getStatusCode());
        assertTrue(postResponse.getBody().contains("rent already closed"));
    }

    @Test
    void givenDelayRent_whenClosingRental_thenCloseRentWithFee() {
        CustomerModel customer = new CustomerModel(null, "Tiago", "1234567890", "12345678901");
        customer = customerRepository.save(customer);

        GameModel game = new GameModel(null, "Jogo Teste", "imagem", 3, 3000);
        game = gameRepository.save(game);

        RentModel rentModel = new RentModel(null, customer, game, LocalDate.now().minusDays(5), 3, null, 3000, 0);

        RentModel saved = rentRepository.save(rentModel);

        ResponseEntity<RentModel> postResponse = restTemplate.exchange(
                "/rentals/" + saved.getId() + "/return",
                HttpMethod.POST,
                null,
                RentModel.class);

        RentModel rent = postResponse.getBody();
        Optional<RentModel> savedRent = rentRepository.findById(rent.getId());

        assertEquals(HttpStatus.OK, postResponse.getStatusCode());
        assertEquals(savedRent.get().getDelayFee(), rent.getDelayFee());
    }

    @Test
    void givenRentalWithWrongId_whenDeletingRental_thenThrowsError() {
        CustomerModel customer = new CustomerModel(null, "Tiago", "1234567890", "12345678901");
        customer = customerRepository.save(customer);

        GameModel game = new GameModel(null, "Jogo Teste", "imagem", 3, 3000);
        game = gameRepository.save(game);

        RentModel rentModel = new RentModel(null, customer, game, LocalDate.now().minusDays(5), 3, null, 3000, 0);

        RentModel saved = rentRepository.save(rentModel);

        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                "/rentals/" + saved.getId() + 1,
                HttpMethod.DELETE,
                null,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, deleteResponse.getStatusCode());
        assertTrue(deleteResponse.getBody().contains("a rent with this id does not exist"));
    }

    @Test
    void givenActiveRental_whenDeletingRental_thenThrowsError() {
        CustomerModel customer = new CustomerModel(null, "Tiago", "1234567890", "12345678901");
        customer = customerRepository.save(customer);

        GameModel game = new GameModel(null, "Jogo Teste", "imagem", 3, 3000);
        game = gameRepository.save(game);

        RentModel rentModel = new RentModel(null, customer, game, LocalDate.now().minusDays(5), 3, null, 3000, 0);

        RentModel saved = rentRepository.save(rentModel);

         ResponseEntity<String> deleteResponse = restTemplate.exchange(
                "/rentals/" + saved.getId(),
                HttpMethod.DELETE,
                null,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, deleteResponse.getStatusCode());
        assertTrue(deleteResponse.getBody().contains("An active rent can not be deleted"));
                
    }

    @Test
    void givenClosedRental_whenDeletingRental_thenDeleteRental(){

         CustomerModel customer = new CustomerModel(null, "Tiago", "1234567890", "12345678901");
        customer = customerRepository.save(customer);

        GameModel game = new GameModel(null, "Jogo Teste", "imagem", 3, 3000);
        game = gameRepository.save(game);

        RentModel rentModel = new RentModel(null, customer, game, LocalDate.now().minusDays(5), 3, LocalDate.now(), 3000, 0);

        RentModel saved = rentRepository.save(rentModel);

         ResponseEntity<String> deleteResponse = restTemplate.exchange(
                "/rentals/" + saved.getId(),
                HttpMethod.DELETE,
                null,
                String.class);

        Optional<RentModel> rentAfterDelete = rentRepository.findById(saved.getId());    
        
         assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertTrue(rentAfterDelete.isEmpty());

    }
}
