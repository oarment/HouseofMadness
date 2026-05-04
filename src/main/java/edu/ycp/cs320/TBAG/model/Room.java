package edu.ycp.cs320.TBAG.model;

public class Room {

    private int roomID;
    private String name;
    private int northRoomId;
    private int southRoomId;
    private int eastRoomId;
    private int westRoomId;
    private String hint;

    // === PUZZLE VARIABLES ===
    private boolean isLocked;
    private String requiredItem;

    private Inventory inventory;

    public Room(int roomID, String name, int northRoomId, int southRoomId, int eastRoomId, int westRoomId) {
        this.roomID = roomID;
        this.name = name;
        this.northRoomId = northRoomId;
        this.southRoomId = southRoomId;
        this.eastRoomId = eastRoomId;
        this.westRoomId = westRoomId;
        this.inventory = new Inventory();

        // Default to unlocked
        this.isLocked = false;
        this.requiredItem = "";
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

    // === MISSING SETTER ADDED ===
    public void setName(String name) {
        this.name = name;
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

    public String getHint() {
        if (hint == null || hint.isEmpty()) {
            return "";
        }
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    // === PUZZLE GETTERS & SETTERS ===
    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public String getRequiredItem() {
        return requiredItem;
    }

    public void setRequiredItem(String requiredItem) {
        this.requiredItem = requiredItem;
    }
}