package edu.ycp.cs320.TBAG.controller;
import edu.ycp.cs320.TBAG.model.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomController {

    public List<Room> map;
    public initializeMap() {
        map = new ArrayList<Room>();
        Room room1 = new Room(1, "Main Hall, Front", 4, 0, 3, 2);
        Room room2 = new Room(2, "Lounge", 0, 0, 1, 0);
        Room room3 = new Room(3, "Library", 0, 0, 0, 1);
        Room room4 = new Room(4, "Main Hall, Middle", 7, 1, 5, 0);
        Room room5 = new Room(5, "Kitchen", 6, 0, 0, 4);
        Room room6 = new Room(6, "Closet", 0, 5, 0, 0);
        Room room7 = new Room(7, "Main Hall, End", 0, 4, 0, 8);
        Room room8 = new Room(8, "Bathroom", 0, 0, 7, 0);
    }

}
