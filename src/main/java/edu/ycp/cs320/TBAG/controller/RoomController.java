package edu.ycp.cs320.TBAG.controller;
import edu.ycp.cs320.TBAG.model.Room;
import edu.ycp.cs320.TBAG.model.Item;

import java.util.ArrayList;
import java.util.List;

public class RoomController {


    public List<Room> map;
    public List<Room> initializeMap() {
        List<Room> map = new ArrayList<>();

        map.add(new Room(1, "Main Hall, Front", 4, 0, 3, 2));
        map.add(new Room(2, "Lounge", 0, 0, 1, 0));
        map.add(new Room(3, "Library", 0, 0, 0, 1));
        map.add(new Room(4, "Main Hall, Middle", 7, 1, 5, 0));
        map.add(new Room(5, "Kitchen", 6, 0, 0, 4));
        map.add(new Room(6, "Closet", 0, 5, 0, 0));
        map.add(new Room(7, "Main Hall, End", 0, 4, 0, 8));
        map.add(new Room(8, "Bathroom", 0, 0, 7, 0));
        map.get(0).getInventory().addItem(new Item("Flashlight", 0));
        map.get(1).getInventory().addItem(new Item("keys", 0));
        map.get(3).getInventory().addItem(new Item("Potion", 20));

        return map;
    }



}
