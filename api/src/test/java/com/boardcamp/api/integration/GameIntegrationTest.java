package com.boardcamp.api.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

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
import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.repositories.RentRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GameIntegrationTest {

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
    void givenGames_whenGettingGames_thenReturnsGames() {

        GameDTO dto1 = new GameDTO("jogo 1", "imagem", 3, 3000);
        HttpEntity<GameDTO> body1 = new HttpEntity<>(dto1);

        ResponseEntity<String> postResponse1 = restTemplate.exchange(
                "/games",
                HttpMethod.POST,
                body1,
                String.class);

        GameDTO dto2 = new GameDTO("jogo 2", "imagem", 5, 4500);
        HttpEntity<GameDTO> body2 = new HttpEntity<>(dto2);

        ResponseEntity<String> postResponse2 = restTemplate.exchange(
                "/games",
                HttpMethod.POST,
                body2,
                String.class);

        ResponseEntity<List<GameModel>> getResponse = restTemplate.exchange(
                "/games",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<GameModel>>() {
                });

        List<GameModel> games = getResponse.getBody();


        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(games);
        assertEquals(2, games.size());
        
        
    }


     @Test
    void givenGame_whenCreatingGame_thenCreateGame() {

        GameDTO dto1 = new GameDTO("jogo 1", "imagem", 3, 3000);
        HttpEntity<GameDTO> body1 = new HttpEntity<>(dto1);

        ResponseEntity<GameModel> postResponse1 = restTemplate.exchange(
                "/games",
                HttpMethod.POST,
                body1,
                GameModel.class);




        GameModel game = postResponse1.getBody();
        Optional<GameModel>  savedGame = gameRepository.findById(game.getId());



        assertEquals(HttpStatus.CREATED, postResponse1.getStatusCode());
        assertEquals(game.getName(), savedGame.get().getName());

    }

    @Test
    void givenExistingGame_whenCreatingGame_thenThrowsError(){
     
        GameDTO dto1 = new GameDTO("jogo 1", "imagem", 3, 3000);
        HttpEntity<GameDTO> body1 = new HttpEntity<>(dto1);

        ResponseEntity<GameModel> postResponse1 = restTemplate.exchange(
                "/games",
                HttpMethod.POST,
                body1,
                GameModel.class);


                 
        HttpEntity<GameDTO> body2 = new HttpEntity<>(dto1);

        ResponseEntity<String> postResponse2 = restTemplate.exchange(
                "/games",
                HttpMethod.POST,
                body2,
                String.class);



        assertEquals(HttpStatus.CONFLICT, postResponse2.getStatusCode());
        assertTrue(postResponse2.getBody().contains("A game with this name already exists"));
    }
}
