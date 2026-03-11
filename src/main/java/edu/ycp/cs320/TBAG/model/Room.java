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




}
