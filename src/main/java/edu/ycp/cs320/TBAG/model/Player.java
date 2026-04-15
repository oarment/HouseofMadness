package edu.ycp.cs320.TBAG.model;

import edu.ycp.cs320.TBAG.model.Inventory;

// model class for Player
// only the controller should be allowed to call the set methods
public class Player extends Actor {
	private Integer roomID;
	private int sanity;
	private String dialog;

    private Inventory inventory;

	public Player() {
		this.name = "Hero";
		this.roomID = 1;
		this.sanity = 100;
		this.health = 100;
		this.damage = 1;
        this.inventory = new Inventory();
	}
	
	public Player(Integer roomID) {
        this.roomID = roomID;
        this.inventory = new Inventory();
	}

    public Inventory getInventory() {
        return inventory;
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
	public String getDialog() {
		return dialog;
	}

	public void setDialog(String dialog) {
		this.dialog = dialog;
	}
}
