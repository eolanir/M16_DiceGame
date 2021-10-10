package com.diceGame.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="games")
public class Game {
	
	@Id
	private String id;
	@Field(name="dice1")
	private int dice1;
	@Field(name="dice2")
	private int dice2;
	@Field(name="result")
	private String result;
	@DBRef
	private User user;
	
	Game() {
		
	}
	
	public Game (int dice1, int dice2, User user) {
		this.dice1 = dice1;
		this.dice2 = dice2;
		this.user = user;
		if(dice1+dice2==7) {
			this.result="Win";
		} else {
			this.result="Lose";
		}
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getDice1() {
		return dice1;
	}
	public void setDice1(int dice1) {
		this.dice1 = dice1;
	}
	public int getDice2() {
		return dice2;
	}
	public void setDice2(int dice2) {
		this.dice2 = dice2;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	
}
