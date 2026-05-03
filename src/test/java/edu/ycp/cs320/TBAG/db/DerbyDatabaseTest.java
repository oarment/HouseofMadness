package edu.ycp.cs320.TBAG.db;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.Player;
import edu.ycp.cs320.TBAG.model.Room;

public class DerbyDatabaseTest {

    private static DerbyDatabase db;

    @BeforeAll
    public static void setUp() {
        db = new DerbyDatabase();
        DatabaseProvider.setInstance(db);
    }

    @Test
    public void testFindPlayer() {
        Player player = db.findPlayer();

        assertNotNull(player);
        assertEquals(100, player.getHealth());
        assertEquals(100, player.getSanity());
        assertEquals(1, player.getDamage());
        assertEquals(1, player.getRoomID());
    }

    @Test
    public void testFindAllRooms() {
        List<Room> rooms = db.findAllRooms();

        assertNotNull(rooms);
        assertEquals(8, rooms.size());
        assertTrue(rooms.stream().anyMatch(r -> r.getRoomID() == 1));
        assertTrue(rooms.stream().anyMatch(r -> r.getRoomID() == 8));
    }

    @Test
    public void testFindAllItems() {
        List<Item> items = db.findAllItems();

        assertNotNull(items);
        assertEquals(5, items.size());

        assertTrue(items.stream().anyMatch(i ->
                i.getName().equalsIgnoreCase("Rusty Key")
                        && i.getType().equalsIgnoreCase("key")));

        assertTrue(items.stream().anyMatch(i ->
                i.getName().equalsIgnoreCase("Health Potion")
                        && i.getType().equalsIgnoreCase("health")));

        assertTrue(items.stream().anyMatch(i ->
                i.getName().equalsIgnoreCase("Crowbar")
                        && i.getType().equalsIgnoreCase("utility")));

        assertTrue(items.stream().anyMatch(i ->
                i.getName().equalsIgnoreCase("Sanity Pills")
                        && i.getType().equalsIgnoreCase("sanity")));

        assertTrue(items.stream().anyMatch(i ->
                i.getName().equalsIgnoreCase("Silver Locket")
                        && i.getType().equalsIgnoreCase("utility")));
    }

    @Test
    public void testFindFullMapLoadsRoomInventories() {
        List<Room> map = db.findFullMap();

        assertNotNull(map);
        assertEquals(8, map.size());

        Room room1 = map.stream().filter(r -> r.getRoomID() == 1).findFirst().orElse(null);
        Room room2 = map.stream().filter(r -> r.getRoomID() == 2).findFirst().orElse(null);
        Room room3 = map.stream().filter(r -> r.getRoomID() == 3).findFirst().orElse(null);
        Room room6 = map.stream().filter(r -> r.getRoomID() == 6).findFirst().orElse(null);
        Room room8 = map.stream().filter(r -> r.getRoomID() == 8).findFirst().orElse(null);

        assertNotNull(room1);
        assertNotNull(room2);
        assertNotNull(room3);
        assertNotNull(room6);
        assertNotNull(room8);

        assertTrue(room1.getInventory().getItems().stream()
                .anyMatch(i -> i.getName().equalsIgnoreCase("Rusty Key")));

        assertTrue(room2.getInventory().getItems().stream()
                .anyMatch(i -> i.getName().equalsIgnoreCase("Health Potion")));

        assertTrue(room3.getInventory().getItems().stream()
                .anyMatch(i -> i.getName().equalsIgnoreCase("Crowbar")));

        assertTrue(room6.getInventory().getItems().stream()
                .anyMatch(i -> i.getName().equalsIgnoreCase("Sanity Pills")));

        assertTrue(room8.getInventory().getItems().stream()
                .anyMatch(i -> i.getName().equalsIgnoreCase("Silver Locket")));
    }
}