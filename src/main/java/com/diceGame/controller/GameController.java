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
import com.diceGame.dao.PlayerRepository;
import com.diceGame.dto.GameDTO;
import com.diceGame.dto.PlayerDTO;
import com.diceGame.model.Game;
import com.diceGame.model.Player;

@RestController
@RequestMapping("/players")
public class GameController {
	
	@Autowired
	private PlayerRepository playerRepo;

	@Autowired
	private GameRepository gameRepo;
	
	//CREATE PLAYER
	@PostMapping
	public ResponseEntity<String> createPlayer(@RequestBody Player player){
		//Create an optional user looking for players with the selected name
		Optional<Player> optionalPlayer = playerRepo.findByName(player.getName());
		
		//If player name is already taken, throw a bad request status
		Player newPlayer;
		if(optionalPlayer.isPresent()) {
			return ResponseEntity.badRequest().body("Player already exist.");
		} else {
			//Create the object and generate the current date to add it as registration date
			LocalDate localDate = LocalDate.now();
			player.setDate(localDate.toString());
			newPlayer = player;
			
			//If the name is empty, create an Anonymous player
			if(player.getName()==null||player.getName().equals("")) {
				player.setName("Anonymous");
				newPlayer = playerRepo.save(player);
			} else {
				newPlayer = playerRepo.save(player);
			}
		}
		return ResponseEntity.ok("Player created as " + newPlayer.getName() + " and Id: " + newPlayer.getId());
	}
	
	//EDIT PLAYER
	@PutMapping
	public ResponseEntity<PlayerDTO> editPlayer(@RequestBody Player form){
		//Get an instance of the player with the selected Id
		Optional<Player> optionalPlayer = playerRepo.findById(form.getId());
		
		if(optionalPlayer.isPresent()) {
			//Find an user with the selected name and if it exists and not "Anonymous", return a bad request
			Optional<Player> tempPlayer = playerRepo.findByName(form.getName());
			if(tempPlayer.isPresent() 
					&& tempPlayer.get().getName().equals(form.getName()) 
					&& !form.getName().equals("Anonymous")) {
				return ResponseEntity.badRequest().build();
			} else {
				//If the name is not used, save and return ok
				if(form.getName().equals("")) {
					form.setName("Anonymous");
				}
				Player updateUser = optionalPlayer.get();
				updateUser.setName(form.getName());
				PlayerDTO dto = new PlayerDTO(updateUser);
				playerRepo.save(updateUser);
				return ResponseEntity.ok(dto);
			}
		} else {
			//If optional is empty, return not found
			return ResponseEntity.notFound().build();
		}
	}
	
	//PLAY GAME
	@PostMapping ("/{player_id}/games")
	public ResponseEntity<GameDTO> playGame(@PathVariable("player_id") String playerId){
		Optional<Player> optionalPlayer = playerRepo.findById(playerId);
		//If the player with selected Id exist, create random numbers from 1 to 6 
		if(optionalPlayer.isPresent()) {
			int a = (int) Math.ceil(Math.random()*6);
			int b = (int) Math.ceil(Math.random()*6);
			//Create and save the game with the User Id
			Game newGame = new Game(a, b, optionalPlayer.get());
			gameRepo.save(newGame);
			optionalPlayer.get().addGame();
			if(newGame.getResult().equals("Win")) {
				optionalPlayer.get().addWins();
			}
			playerRepo.save(optionalPlayer.get());
			GameDTO dto = new GameDTO(newGame.getId(), newGame.getDice1(), newGame.getDice2(), newGame.getResult());
			return ResponseEntity.ok(dto);
		} else {
			//If there is no player with the selected Id, return not found
			return ResponseEntity.notFound().build();
		}
	}
	
	//DELETE GAMES
	@DeleteMapping ("/{player_id}/games")
	public ResponseEntity<String> deleteGames(@PathVariable("player_id") String playerId){
		//Find all games by the player Id and delete them
		Optional<Player> user = playerRepo.findById(playerId);
		if(user.isPresent()) {
			List<Game> games = gameRepo.findAllByUser(user.get());
			user.get().setWins(0);
			user.get().setGames(0);
			gameRepo.deleteAll(games);
			playerRepo.save(user.get());
			return ResponseEntity.ok("Deleted games from the player with Id " + playerId);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	//FIND ALL PLAYERS
	@GetMapping
	public ResponseEntity<List<PlayerDTO>> getPlayers(){
		//Find all users
		List<Player> players = playerRepo.findAll();
		List<PlayerDTO> dto = new ArrayList<PlayerDTO>();
		for(Player i : players) {
			dto.add(new PlayerDTO(i));
		}
		return ResponseEntity.ok(dto);
	}
	
	//FIND PLAYER GAMES
	@GetMapping("/{player_id}/games")
	public ResponseEntity<List<GameDTO>> getPlayerGames(@PathVariable("player_id") String playerId){
		Optional<Player> optionalUser = playerRepo.findById(playerId);
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
	public ResponseEntity<PlayerDTO> getLoser(){
		List<Player> players = playerRepo.findAll();
		List<PlayerDTO> dtoList = new ArrayList<PlayerDTO>();
		for(Player i : players) {
			dtoList.add(new PlayerDTO(i));
		}
		Collections.sort(dtoList, (a, b) -> Double.compare(Double.parseDouble(b.getRate().replace("%", ""))
														, Double.parseDouble(a.getRate().replace("%", ""))));
		Collections.reverse(dtoList);
		PlayerDTO loser = dtoList.get(0);
		return ResponseEntity.ok(loser);
	}
	
	//GET WINNER
	@GetMapping("/ranking/winner")
	public ResponseEntity<PlayerDTO> getWinner(){
		List<Player> players = playerRepo.findAll();
		List<PlayerDTO> dtoList = new ArrayList<PlayerDTO>();
		for(Player i : players) {
			dtoList.add(new PlayerDTO(i));
		}
		Collections.sort(dtoList, (a, b) -> Double.compare(Double.parseDouble(b.getRate().replace("%", ""))
														, Double.parseDouble(a.getRate().replace("%", ""))));
		PlayerDTO winner = dtoList.get(0);
		return ResponseEntity.ok(winner);
	}
}
