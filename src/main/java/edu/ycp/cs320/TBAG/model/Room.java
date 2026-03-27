package edu.ycp.cs320.TBAG.model;

public class Room {

    private int roomID;
    private String name;
    private int northRoomId;
    private int southRoomId;
    private int eastRoomId;
    private int westRoomId;

    private Inventory inventory;

    public Room(int roomID, String name, int northRoomId, int southRoomId, int eastRoomId, int westRoomId) {
        this.roomID = roomID;
        this.name = name;
        this.northRoomId = northRoomId;
        this.southRoomId = southRoomId;
        this.eastRoomId = eastRoomId;
        this.westRoomId = westRoomId;
        this.inventory = new Inventory();
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getRoomID() {
        return roomID;
    }

    public String getName() {
        return name;
    }

    public int getNorth() {
        return northRoomId;
    }

    public int getSouth() {
        return southRoomId;
    }

    public int getEast() {
        return eastRoomId;
    }

    public int getWest() {
        return westRoomId;
    }
}