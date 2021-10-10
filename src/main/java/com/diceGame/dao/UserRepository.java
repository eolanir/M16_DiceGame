package com.diceGame.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.diceGame.model.User;

public interface UserRepository extends MongoRepository<User, String> {
	Optional<User> findByName(String name);
}
