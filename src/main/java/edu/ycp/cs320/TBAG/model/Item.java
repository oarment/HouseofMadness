package edu.ycp.cs320.TBAG.model;

public class Item {
    private int id;
    private String name;
    private String type;
    private int effect;
    private int roomID;

    public Item(int id, String name, String type, int effect, int roomID) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.effect = effect;
        this.roomID = roomID;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getEffect() {
        return effect;
    }

    public void setEffect(int effect) {
        this.effect = effect;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }
}