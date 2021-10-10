package com.diceGame.dto;

public class GameDTO {
	private String id;
	private int dice1;
	private int dice2;
	private String result;
	
	public GameDTO(String id, int dice1, int dice2, String result) {
		this.id = id;
		this.dice1 = dice1;
		this.dice2 = dice2;
		this.result = result;
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
	
}
