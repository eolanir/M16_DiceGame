package com.diceGame.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diceGame.dao.GameRepository;
import com.diceGame.dao.UserRepository;
import com.diceGame.dto.GameDTO;
import com.diceGame.dto.UserDTO;
import com.diceGame.model.Game;
import com.diceGame.model.User;

@RestController
@RequestMapping("/players")
public class GameController {
	
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private GameRepository gameRepo;
	
	//CREATE PLAYER
	@PostMapping
	public ResponseEntity<String> createUser(@RequestBody User user){
		//Create an optional user looking for players with the selected name
		Optional<User> optionalUser = userRepo.findByName(user.getName());
		
		//If player name is already taken, throw a bad request status
		User newUser;
		if(optionalUser.isPresent()) {
			return ResponseEntity.badRequest().body("Player already exist.");
		} else {
			//Create the object and generate the current date to add it as registration date
			LocalDate localDate = LocalDate.now();
			user.setDate(localDate.toString());
			newUser = user;
			
			//If the name is empty, create an Anonymous player
			if(user.getName()==null||user.getName().equals("")) {
				user.setName("Anonymous");
				newUser = userRepo.save(user);
			} else {
				newUser = userRepo.save(user);
			}
		}
		return ResponseEntity.ok("Player created as " + newUser.getName() + " and Id: " + newUser.getId());
	}
	
	//EDIT PLAYER
	@PutMapping
	public ResponseEntity<UserDTO> editUser(@RequestBody User form){
		//Get an instance of the player with the selected Id
		Optional<User> optionalUser = userRepo.findById(form.getId());
		
		if(optionalUser.isPresent()) {
			//Find an user with the selected name and if it exists and not "Anonymous", return a bad request
			Optional<User> tempUser = userRepo.findByName(form.getName());
			if(tempUser.isPresent() 
					&& tempUser.get().getName().equals(form.getName()) 
					&& !form.getName().equals("Anonymous")) {
				return ResponseEntity.badRequest().build();
			} else {
				//If the name is not used, save and return ok
				if(form.getName().equals("")) {
					form.setName("Anonymous");
				}
				User updateUser = optionalUser.get();
				updateUser.setName(form.getName());
				UserDTO dto = new UserDTO(updateUser);
				userRepo.save(updateUser);
				return ResponseEntity.ok(dto);
			}
		} else {
			//If optional is empty, return not found
			return ResponseEntity.notFound().build();
		}
	}
	
	//PLAY GAME
	@PostMapping ("/{user_id}/games")
	public ResponseEntity<GameDTO> playGame(@PathVariable("user_id") String userId){
		Optional<User> optionalUser = userRepo.findById(userId);
		//If the player with selected Id exist, create random numbers from 1 to 6 
		if(optionalUser.isPresent()) {
			int a = (int) Math.ceil(Math.random()*6);
			int b = (int) Math.ceil(Math.random()*6);
			//Create and save the game with the User Id
			Game newGame = new Game(a, b, optionalUser.get());
			gameRepo.save(newGame);
			optionalUser.get().addGame();
			if(newGame.getResult().equals("Win")) {
				optionalUser.get().addWins();
			}
			userRepo.save(optionalUser.get());
			GameDTO dto = new GameDTO(newGame.getId(), newGame.getDice1(), newGame.getDice2(), newGame.getResult());
			return ResponseEntity.ok(dto);
		} else {
			//If there is no player with the selected Id, return not found
			return ResponseEntity.notFound().build();
		}
	}
	
	//DELETE GAMES
	@DeleteMapping ("/{user_id}/games")
	public ResponseEntity<String> deleteGames(@PathVariable("user_id") String userId){
		//Find all games by the player Id and delete them
		Optional<User> user = userRepo.findById(userId);
		if(user.isPresent()) {
			List<Game> games = gameRepo.findAllByUser(user.get());
			user.get().setWins(0);
			user.get().setGames(0);
			gameRepo.deleteAll(games);
			userRepo.save(user.get());
			return ResponseEntity.ok("Deleted games from the player with Id " + userId);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	//FIND ALL PLAYERS
	@GetMapping
	public ResponseEntity<List<UserDTO>> getUsers(){
		//Find all users
		List<User> users = userRepo.findAll();
		List<UserDTO> dto = new ArrayList<UserDTO>();
		for(User i : users) {
			dto.add(new UserDTO(i));
		}
		return ResponseEntity.ok(dto);
	}
	
	//FIND PLAYER GAMES
	@GetMapping("/{user_id}/games")
	public ResponseEntity<List<GameDTO>> getUserGames(@PathVariable("user_id") String userId){
		Optional<User> optionalUser = userRepo.findById(userId);
		if(optionalUser.isPresent()) {
			List<Game> games = gameRepo.findAllByUser(optionalUser.get());
			List<GameDTO> dto = new ArrayList<GameDTO>();
			for(Game i : games) {
				dto.add(new GameDTO(i.getId(), i.getDice1(), i.getDice2(), i.getResult()));
			}
			return ResponseEntity.ok(dto);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	//RANKING
	@GetMapping("/ranking")
	public ResponseEntity<String> getRanking(){
		List<Game> games = gameRepo.findAll();
		List<Game> win = gameRepo.findAllByResult("Win");
		double rate;
		if(games.isEmpty()) {
			rate = 0;
		} else {
			double wins = win.size();
			double igames = games.size();	 
			rate = wins/igames*100;
		}
		
		return ResponseEntity.ok("Global rate: " + rate + "%");
	}
	
	//GET LOSER
	@GetMapping("/ranking/loser")
	public ResponseEntity<UserDTO> getLoser(){
		List<User> users = userRepo.findAll();
		List<UserDTO> dtoList = new ArrayList<UserDTO>();
		for(User i : users) {
			dtoList.add(new UserDTO(i));
		}
		Collections.sort(dtoList, (a, b) -> Double.compare(Double.parseDouble(b.getRate().replace("%", ""))
														, Double.parseDouble(a.getRate().replace("%", ""))));
		Collections.reverse(dtoList);
		UserDTO loser = dtoList.get(0);
		return ResponseEntity.ok(loser);
	}
	
	//GET WINNER
	@GetMapping("/ranking/winner")
	public ResponseEntity<UserDTO> getWinner(){
		List<User> users = userRepo.findAll();
		List<UserDTO> dtoList = new ArrayList<UserDTO>();
		for(User i : users) {
			dtoList.add(new UserDTO(i));
		}
		Collections.sort(dtoList, (a, b) -> Double.compare(Double.parseDouble(b.getRate().replace("%", ""))
														, Double.parseDouble(a.getRate().replace("%", ""))));
		UserDTO winner = dtoList.get(0);
		return ResponseEntity.ok(winner);
	}
}
