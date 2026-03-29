package edu.ycp.cs320.TBAG.model;

// model class for Player
// only the controller should be allowed to call the set methods
public class Player extends Actor {

	private int sanity;

	public Player() {
		this.name = "Hero";
		this.roomID = 1;
		this.sanity = 100;
		this.health = 100;
		this.damage = 1;
	}
	public Integer getSanity() {return sanity;}

	public void setSanity(int s) {sanity = s;}

}
