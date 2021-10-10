package com.diceGame.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.diceGame.model.Player;

public interface PlayerRepository extends MongoRepository<Player, String> {
	Optional<Player> findByName(String name);
}
