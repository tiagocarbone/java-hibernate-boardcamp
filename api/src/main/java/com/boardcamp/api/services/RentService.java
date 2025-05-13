package com.boardcamp.api.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;


import com.boardcamp.api.dtos.RentDTO;
import com.boardcamp.api.exceptions.customer_exceptions.CustomerNotFoundException;
import com.boardcamp.api.exceptions.game_exceptions.GameNotFoundException;
import com.boardcamp.api.exceptions.game_exceptions.GameStockException;
import com.boardcamp.api.exceptions.rent_exceptions.RentActiveException;
import com.boardcamp.api.exceptions.rent_exceptions.RentClosedException;
import com.boardcamp.api.exceptions.rent_exceptions.RentNotFoundException;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.models.RentModel;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.repositories.RentRepository;

@Service
public class RentService {

    final RentRepository rentRepository;
    final GameRepository gameRepository;
    final CustomerRepository customerRepository;

    public RentService(RentRepository rentRepository, GameRepository gameRepository,
            CustomerRepository customerRepository) {
        this.rentRepository = rentRepository;
        this.gameRepository = gameRepository;
        this.customerRepository = customerRepository;
    }

    public List<RentModel> getAllRents(){
        return rentRepository.findAll();
    }


    public RentModel createRental(RentDTO body) {

        GameModel game = gameRepository
                .findById(body.getGameId())
                .orElseThrow(() -> new GameNotFoundException("a game with this id does not exist"));

        CustomerModel customer = customerRepository
                .findById(body.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("a customer with this id does not exist"));

        int daysRented = body.getDaysRented();
        int pricePerDay = game.getPricePerDay();
        int originalPrice = daysRented * pricePerDay;

        RentModel rent = new RentModel(body);
        rent.setOriginalPrice(originalPrice);
        rent.setCustomer(customer);
        rent.setGame(game);
        verifyStock(game);
        game.setStockTotal(game.getStockTotal() - 1);

        return rentRepository.save(rent);
    }

    public RentModel closeRental(Long id) {
        RentModel rent = rentRepository
                .findById(id)
                .orElseThrow(() -> new RentNotFoundException("a rent with this id does not exist"));

        if (rent.getReturnDate() != null) {
            throw new RentClosedException("rent already closed");
        }

        // Data de devolução hoje
        LocalDate returnDate = LocalDate.now();
        rent.setReturnDate(returnDate);

        // Data esperada de devolução = data do aluguel + dias alugados
        LocalDate expectedReturnDate = rent.getRentDate().plusDays(rent.getDaysRented());

        // Calcula dias de atraso (se houver)
        long daysLate = ChronoUnit.DAYS.between(expectedReturnDate, returnDate);

        if (daysLate > 0) {
            int pricePerDay = rent.getGame().getPricePerDay();
            rent.setDelayFee((int) daysLate * pricePerDay);
        } else {
            rent.setDelayFee(0);
        }

        return rentRepository.save(rent);
    }

    public void deleteRental(Long id) {

        RentModel rent = rentRepository
            .findById(id)
            .orElseThrow(() -> new RentNotFoundException("a rent with this id does not exist"));

            if(rent.getReturnDate() == null){
                throw new RentActiveException("An active rent can not be deleted");
            }

        rentRepository.delete(rent);

    }

    private void verifyStock(GameModel game) {
        if (game.getStockTotal() < 1) {
            throw new GameStockException("this game has no stock to rent");
        }
    }

}
