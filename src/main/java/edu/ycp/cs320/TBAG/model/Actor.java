package edu.ycp.cs320.TBAG.model;

public abstract class Actor {
    protected Integer roomID;
    protected String name;
    protected int health;
    protected int damage;

    public Integer getRoomID() {
        return roomID;
    }
    public Integer getHealth() {return health;}
    public String getName() {return name;}
    public Integer getDamage() {return damage;}

    public void setRoomID(Integer roomID) {
        this.roomID = roomID;
    }
    public void setHealth(int hp) {health = hp;}
    public void setDamage(int dmg) {damage = dmg;}


}
