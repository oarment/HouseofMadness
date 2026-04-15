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
        assertEquals(3, items.size());
        assertTrue(items.stream().anyMatch(i -> i.getName().equalsIgnoreCase("Flashlight")));
        assertTrue(items.stream().anyMatch(i -> i.getName().equalsIgnoreCase("keys")));
        assertTrue(items.stream().anyMatch(i -> i.getName().equalsIgnoreCase("Potion")));
    }

    @Test
    public void testFindFullMapLoadsRoomInventories() {
        List<Room> map = db.findFullMap();

        assertNotNull(map);
        assertEquals(8, map.size());

        Room room1 = map.stream().filter(r -> r.getRoomID() == 1).findFirst().orElse(null);
        Room room2 = map.stream().filter(r -> r.getRoomID() == 2).findFirst().orElse(null);
        Room room4 = map.stream().filter(r -> r.getRoomID() == 4).findFirst().orElse(null);

        assertNotNull(room1);
        assertNotNull(room2);
        assertNotNull(room4);

        assertTrue(room1.getInventory().getItems().stream()
                .anyMatch(i -> i.getName().equalsIgnoreCase("Flashlight")));
        assertTrue(room2.getInventory().getItems().stream()
                .anyMatch(i -> i.getName().equalsIgnoreCase("keys")));
        assertTrue(room4.getInventory().getItems().stream()
                .anyMatch(i -> i.getName().equalsIgnoreCase("Potion")));
    }
}