package com.diceGame.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.diceGame.model.Game;
import com.diceGame.model.User;

public interface GameRepository extends MongoRepository<Game, String>{
	List<Game> findAllByUser(User user);
	List<Game> findAllByResult(String result);
}
