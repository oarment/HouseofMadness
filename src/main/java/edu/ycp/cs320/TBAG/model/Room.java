package edu.ycp.cs320.TBAG.model;

public class Room {

    private int roomID;
    private String name;
    private int northRoomId;
    private int southRoomId;
    private int eastRoomId;
    private int westRoomId;


    public Room(int  roomID, String name, int northRoomId, int southRoomId, int eastRoomId, int westRoomId ) {
        this.roomID = roomID;
        this.name = name;
        this.northRoomId = northRoomId;
        this.southRoomId = southRoomId;
        this.eastRoomId = eastRoomId;
        this.westRoomId =  westRoomId;
    }

    public String getName(int roomID){
        return name;
    }

    public int getNorth(int roomID) {return northRoomId;}
    public int getSouth(int roomID) {return southRoomId;}
    public int getEast(int roomID) {return eastRoomId;}
    public int getWest(int roomID) {return westRoomId;}




}
