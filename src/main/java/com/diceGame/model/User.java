package com.diceGame.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="users")
public class User {
	
	@Id
	private String id;
	@Field(name="name")
	private String name;
	@Field(name="date")
	private String date;
	@Field(name="wins")
	private int wins = 0;
	@Field(name="games")
	private int games = 0;

	public void addGame() {
		games++;
	}
	public int getGames() {
		return games;
	}
	public void setGames(int games) {
		this.games = games;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public double getWins() {
		return wins;
	}
	public void setWins(int wins) {
		this.wins = wins;
	}
	public void addWins() {
		wins++;
	}

}
