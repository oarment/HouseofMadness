package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.Room;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MapGenerator {

    public static List<Room> generateRandomMap() {
        List<Room> map = new ArrayList<>();

        Room r1 = new Room(1, "Foyer", 2, 0, 7, 0);
        Room r2 = new Room(2, "Room 2", 3, 1, 8, 0);
        Room r3 = new Room(3, "Room 3", 4, 2, 9, 0);
        Room r4 = new Room(4, "Room 4", 5, 3, 10, 0);
        Room r5 = new Room(5, "Room 5", 6, 4, 11, 0);
        Room r6 = new Room(6, "Room 6", 0, 5, 12, 0);

        Room r7 = new Room(7, "Room 7", 0, 0, 0, 1);
        Room r8 = new Room(8, "Room 8", 0, 0, 0, 2);
        Room r9 = new Room(9, "Room 9", 0, 0, 0, 3);
        Room r10 = new Room(10, "Room 10", 0, 0, 0, 4);
        Room r11 = new Room(11, "Room 11", 0, 0, 0, 5);
        Room r12 = new Room(12, "Room 12", 0, 0, 0, 6);

        List<String> themes = Arrays.asList(
                "The Meat Locker", "Creepy Nursery", "Wine Cellar", "Boiler Room",
                "Dusty Attic", "Overgrown Conservatory", "Abandoned Library",
                "Master Bedroom", "Dungeon", "Chapel", "Servant's Quarters"
        );
        Collections.shuffle(themes);

        r2.setName(themes.get(0));
        r3.setName(themes.get(1));
        r4.setName(themes.get(2));
        r5.setName(themes.get(3));
        r6.setName(themes.get(4));
        r7.setName(themes.get(5));
        r8.setName(themes.get(6));
        r9.setName(themes.get(7));
        r10.setName(themes.get(8));
        r11.setName(themes.get(9));
        r12.setName(themes.get(10));

        r4.setLocked(true);
        r4.setRequiredItem("rusty key");

        map.add(r1); map.add(r2); map.add(r3); map.add(r4);
        map.add(r5); map.add(r6); map.add(r7); map.add(r8);
        map.add(r9); map.add(r10); map.add(r11); map.add(r12);

        return map;
    }

    public static void populateItems(List<Room> map) {
        Random rand = new Random();
        int[] safeRooms = {1, 2, 3, 7, 8, 9};

        // 1. Place the Puzzle Box
        int boxRoomId = safeRooms[rand.nextInt(safeRooms.length)];
        Item box = new Item("Puzzle Box", 0, 100);
        map.get(boxRoomId - 1).getInventory().addItem(box);

        // 2. Generate a random 4-digit PIN and hide it in the Torn Note
        int pinCode = 1000 + rand.nextInt(9000);
        int noteRoomId = safeRooms[rand.nextInt(safeRooms.length)];
        Item note = new Item("Torn Note", pinCode, 101);
        map.get(noteRoomId - 1).getInventory().addItem(note);

        // 3. Randomize Consumable Loot
        // Guarantee at least 1 of each spawns in the house
        map.get(rand.nextInt(12)).getInventory().addItem(new Item("Health Potion", 25, 102));
        map.get(rand.nextInt(12)).getInventory().addItem(new Item("Sanity Pills", 15, 103));

        // 25% chance for each room to spawn an extra item
        int extraId = 104;
        for (int i = 0; i < 12; i++) {
            if (rand.nextDouble() < 0.25) { // 25% chance
                if (rand.nextBoolean()) {
                    map.get(i).getInventory().addItem(new Item("Health Potion", 25, extraId++));
                } else {
                    map.get(i).getInventory().addItem(new Item("Sanity Pills", 15, extraId++));
                }
            }
        }
    }
}