package com.diceGame.dto;

import com.diceGame.model.User;

public class UserDTO {
	private String id;
	private String name;
	private String date;
	private String rate;
	
	public UserDTO(String id, String name, String date, double rate) {
		this.id=id;
		this.name=name;
		this.date=date;
		this.rate=rate + "%";
	}
	
	public UserDTO(User user) {
		this.id = user.getId();
		this.name = user.getName();
		this.date = user.getDate();
		if(user.getGames()==0) {
			this.rate = "0%";
		} else {
		this.rate = user.getWins()/user.getGames()*100 + "%";
		}
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

	public String getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate+"%";
	}
	
	
}
