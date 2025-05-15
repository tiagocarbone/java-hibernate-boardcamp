package com.boardcamp.api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.boardcamp.api.dtos.RentDTO;
import com.boardcamp.api.exceptions.customer_exceptions.CustomerNotFoundException;
import com.boardcamp.api.exceptions.game_exceptions.GameNotFoundException;
import com.boardcamp.api.exceptions.game_exceptions.GameStockException;
import com.boardcamp.api.exceptions.rent_exceptions.RentClosedException;
import com.boardcamp.api.exceptions.rent_exceptions.RentNotFoundException;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.models.RentModel;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.repositories.RentRepository;
import com.boardcamp.api.services.RentService;

@SpringBootTest
class RentServiceUnitTests {

    @InjectMocks
    private RentService rentService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RentRepository rentRepository;

  

    @Test
    void givenGameIdThatDoesNotExists_whenCreatingRental_thenThrowsError() {

        RentDTO rent = new RentDTO(1L, 1L, 5);

        doReturn(Optional.empty()).when(gameRepository).findById(rent.getGameId());

        GameNotFoundException exception = assertThrows(GameNotFoundException.class,
                () -> rentService.createRental(rent));

        verify(gameRepository, times(0)).save(any());
        verify(gameRepository, times(1)).findById(rent.getGameId());
        assertNotNull(exception);
        assertEquals("a game with this id does not exist", exception.getMessage());

    }

    @Test
    void givenCustomerIdThatDoesNotExists_whenCreatingRental_thenThrowsError() {

        RentDTO rent = new RentDTO(1L, 1L, 5);
        GameModel gameModel = new GameModel(1L, "jogo", "imagem", 3, 3000);
        CustomerModel customerModel = new CustomerModel(1L, "cliente", "1234567891", "09876442111");

        doReturn(Optional.of(gameModel)).when(gameRepository).findById(rent.getGameId());

        doReturn(Optional.empty()).when(customerRepository).findById(rent.getCustomerId());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
                () -> rentService.createRental(rent));

        verify(gameRepository, times(0)).save(any());
        verify(gameRepository, times(1)).findById(rent.getGameId());
        verify(customerRepository, times(1)).findById(rent.getCustomerId());
        assertNotNull(exception);
        assertEquals("a customer with this id does not exist", exception.getMessage());

    }

    @Test
    void givenGameThatIsOutOfStock_whenCreatingRental_thenThrowsError() {

        RentDTO rent = new RentDTO(1L, 1L, 5);
        GameModel gameModel = new GameModel(1L, "jogo", "imagem", 0, 3000);
        CustomerModel customerModel = new CustomerModel(1L, "cliente", "1234567891", "09876442111");

        doReturn(Optional.of(gameModel)).when(gameRepository).findById(rent.getGameId());
        doReturn(Optional.of(customerModel)).when(customerRepository).findById(rent.getCustomerId());

        GameStockException exception = assertThrows(GameStockException.class,
                () -> rentService.createRental(rent));

        verify(gameRepository, times(0)).save(any());
        verify(gameRepository, times(1)).findById(rent.getGameId());
        verify(customerRepository, times(1)).findById(rent.getCustomerId());
        assertNotNull(exception);
        assertEquals("this game has no stock to rent", exception.getMessage());

    }

    @Test
    void givenGameAndCustomer_whenCreatingRental_thenCreatesRentalSuccessfully() {

        RentDTO rent = new RentDTO(1L, 1L, 5); // 5 dias
        GameModel gameModel = new GameModel(1L, "jogo", "imagem", 3, 3000); // estoque = 3, preço = 3000
        CustomerModel customerModel = new CustomerModel(1L, "cliente", "1234567891", "09876442111");

        doReturn(Optional.of(gameModel)).when(gameRepository).findById(1L);
        doReturn(Optional.of(customerModel)).when(customerRepository).findById(1L);

        RentModel savedRent = new RentModel(rent);
        savedRent.setId(1L);
        savedRent.setGame(gameModel);
        savedRent.setCustomer(customerModel);
        savedRent.setOriginalPrice(5 * 3000);
        doReturn(savedRent).when(rentRepository).save(any());

        RentModel result = rentService.createRental(rent);

        assertNotNull(result);
        assertEquals(15000, result.getOriginalPrice());
        assertEquals("cliente", result.getCustomer().getName());
        assertEquals("jogo", result.getGame().getName());

        assertEquals(2, gameModel.getStockTotal());

        verify(gameRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).findById(1L);
        verify(rentRepository, times(1)).save(any());
    }


    @Test
    void givenRentalThatNotExists_whenClosingRental_thenThrowsError() {
        

        doReturn(Optional.empty()).when(rentRepository).findById(any());

        RentNotFoundException exception = assertThrows(RentNotFoundException.class,
                () -> rentService.closeRental(any()));

        verify(rentRepository, times(1)).findById(any());
        assertNotNull(exception);
        assertEquals("a rent with this id does not exist", exception.getMessage());

    }

    @Test
    void givenRentalThatIsAlreadyClosed_whenClosingRental_thenThrowsError() {
        GameModel gameModel = new GameModel(1L, "jogo", "imagem", 3, 3000); // estoque = 3, preço = 3000
        CustomerModel customerModel = new CustomerModel(1L, "cliente", "1234567891", "09876442111");
        RentModel rent = new RentModel(1L, customerModel, gameModel, LocalDate.now(), 3, LocalDate.now(), 3000, 0);

        doReturn(Optional.of(rent)).when(rentRepository).findById(any());

        RentClosedException exception = assertThrows(RentClosedException.class,
                () -> rentService.closeRental(any()));

        verify(rentRepository, times(1)).findById(any());
        assertNotNull(exception);
        assertEquals("rent already closed", exception.getMessage());
    }

    @Test
    void givenRentalWithDelay_whenClosingRental_thenCalculateCorrectDelayFee() {

        LocalDate rentDate = LocalDate.now().minusDays(5);
        int daysRented = 3;
        LocalDate returnDate = LocalDate.now();
        int pricePerDay = 1500;

        long daysLate = ChronoUnit.DAYS.between(rentDate.plusDays(daysRented), returnDate);
        int expectedFee = (int) daysLate * pricePerDay;

        GameModel gameModel = new GameModel(1L, "jogo", "imagem", 3, pricePerDay);
        CustomerModel customerModel = new CustomerModel(1L, "cliente", "1234567891", "09876442111");

        RentModel rent = new RentModel(1L, customerModel, gameModel, rentDate, daysRented, null, 0, 0);

        when(rentRepository.findById(1L)).thenReturn(Optional.of(rent));

        RentModel savedRent = new RentModel(1L, customerModel, gameModel, rentDate, daysRented, returnDate, 0, expectedFee);
        when(rentRepository.save(any())).thenReturn(savedRent);

        RentModel result = rentService.closeRental(1L);

        assertNotNull(result.getReturnDate());
        assertEquals(expectedFee, result.getDelayFee());
        assertEquals(2, daysLate);

        verify(rentRepository, times(1)).findById(1L);
        verify(rentRepository, times(1)).save(any());
    }
    

}
