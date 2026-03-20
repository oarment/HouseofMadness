package edu.ycp.cs320.TBAG.model;

// model class for Player
// only the controller should be allowed to call the set methods
public class Player extends Actor {
	private Integer roomID;
	private int sanity;

	public Player() {
		this.name = "Hero";
		this.roomID = 1;
		this.sanity = 100;
		this.health = 100;
		this.damage = 1;
	}
	
	public Player(Integer roomID) {
		this.roomID = roomID;
	}




	public Integer getRoomID() {
		return roomID;
	}
	public Integer getHealth() {return health;}
	public Integer getSanity() {return sanity;}
	public String getName() {return name;}
	public Integer getDamage() {return damage;}

	public void setSanity(int s) {sanity = s;}
	public void setRoomID(Integer roomID) {
		this.roomID = roomID;
	}
	public void setHealth(int hp) {health = hp;}
	public void setDamage(int dmg) {damage = dmg;}
}
