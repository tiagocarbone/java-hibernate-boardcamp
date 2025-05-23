package com.boardcamp.api.repositories;

import org.springframework.stereotype.Repository;

import com.boardcamp.api.models.GameModel;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface GameRepository extends JpaRepository<GameModel, Long> {

    boolean existsByName(String name); 

    
}
