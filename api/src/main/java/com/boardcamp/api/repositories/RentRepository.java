package com.boardcamp.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.boardcamp.api.models.RentModel;


@Repository
public interface RentRepository extends JpaRepository<RentModel, Long> {
    
}
