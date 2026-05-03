package edu.ycp.cs320.TBAG.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ItemTest {
    private Item potion;

    @BeforeEach
    public void setUp() {
        potion = new Item(100, "Health Potion", "health", 25, 2);
    }

    @Test
    public void testGetters() {
        assertEquals(100, potion.getID());
        assertEquals("Health Potion", potion.getName());
        assertEquals("health", potion.getType());
        assertEquals(25, potion.getEffect());
        assertEquals(2, potion.getRoomID());
    }

    @Test
    public void testSetters() {
        potion.setID(999);
        potion.setName("Poison");
        potion.setType("health");
        potion.setEffect(-10);
        potion.setRoomID(-1);

        assertEquals(999, potion.getID());
        assertEquals("Poison", potion.getName());
        assertEquals("health", potion.getType());
        assertEquals(-10, potion.getEffect());
        assertEquals(-1, potion.getRoomID());
    }
}