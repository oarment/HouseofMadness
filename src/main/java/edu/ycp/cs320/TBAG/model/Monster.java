package edu.ycp.cs320.TBAG.model;

public class Monster extends Actor {

    private String damageType;

    public Monster(String name, int health, int damage, int roomID, String damageType) {
        this.name = name;
        this.health = health;
        this.damage = damage;
        this.roomID = roomID;
        this.damageType = damageType;
    }

    public String getDamageType() { return damageType; }

}