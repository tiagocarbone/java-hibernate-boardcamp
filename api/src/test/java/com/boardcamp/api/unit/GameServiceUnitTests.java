package com.boardcamp.api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.exceptions.game_exceptions.*;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.services.GameService;

@SpringBootTest
class GameServiceUnitTests {

    @InjectMocks
    private GameService gameService;

    @Mock
    private GameRepository gameRepository;


    @Test
    void whenGetGamesthenShowGames() {
        GameModel game1 = new GameModel(1L, "jogo 1", "imagem", 3, 3000);
        GameModel game2 = new GameModel(2L, "jogo 2", "imagem", 3, 3000);
        List<GameModel> games = List.of(game1, game2);

        when(gameRepository.findAll()).thenReturn(games);

        List<GameModel> result = gameService.getGames();

        verify(gameRepository, times(1)).findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("jogo 1", result.get(0).getName());
        assertEquals("jogo 2", result.get(1).getName());

    }

 

    @Test
    void givenRepeatedName_whenCreatingGame_thenThrowsError() {
        GameDTO game = new GameDTO("jogo", "imagem", 3, 3000);

        doReturn(true).when(gameRepository).existsByName(any());

        GameNameConflictException exception = assertThrows(GameNameConflictException.class,
                () -> gameService.createGame(game));

        verify(gameRepository, times(0)).save(any());
        verify(gameRepository, times(1)).existsByName(any());
        assertNotNull(exception);
        assertEquals("A game with this name already exists", exception.getMessage());

    }

    @Test
    void givenGame_whenCreatingGame_thenCreateGame() {
        GameDTO game = new GameDTO("jogo", "imagem", 3, 3000);

        when(gameRepository.existsByName("jogo")).thenReturn(false);

        GameModel savedGame = new GameModel(1L, "jogo", "imagem", 3, 3000);
        when(gameRepository.save(any())).thenReturn(savedGame);

        GameModel result = gameService.createGame(game);

        verify(gameRepository, times(1)).existsByName("jogo");
        verify(gameRepository, times(1)).save(any());

        assertNotNull(result);
        assertEquals("jogo", result.getName());
        assertEquals("imagem", result.getImage());
        assertEquals(3, result.getStockTotal());
        assertEquals(3000, result.getPricePerDay());
    }

}
