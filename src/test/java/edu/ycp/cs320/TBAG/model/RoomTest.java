package edu.ycp.cs320.TBAG.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RoomTest {
    private Room room;

    @BeforeEach
    public void setUp() {
        // id, name, north, south, east, west
        room = new Room(1, "Foyer", 2, 3, 0, 0);
    }

    @Test
    public void testRoomDirections() {
        assertEquals(1, room.getRoomID());
        assertEquals("Foyer", room.getName());
        assertEquals(2, room.getNorth());
        assertEquals(3, room.getSouth());
        assertEquals(0, room.getEast());
        assertEquals(0, room.getWest());
    }

    @Test
    public void testLockingMechanics() {
        assertFalse(room.isLocked()); // Default should be false
        assertEquals("", room.getRequiredItem()); // Default should be empty string

        room.setLocked(true);
        room.setRequiredItem("Rusty Key");

        assertTrue(room.isLocked());
        assertEquals("Rusty Key", room.getRequiredItem());
    }

    @Test
    public void testRoomInventory() {
        assertNotNull(room.getInventory());
        assertTrue(room.getInventory().isEmpty());
    }
}