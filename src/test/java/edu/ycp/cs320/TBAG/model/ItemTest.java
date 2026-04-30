package edu.ycp.cs320.TBAG.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ItemTest {
    private Item potion;

    @BeforeEach
    public void setUp() {
        potion = new Item("Health Potion", 25, 100);
    }

    @Test
    public void testGetters() {
        assertEquals("Health Potion", potion.getName());
        assertEquals(25, potion.getEffect());
        assertEquals(100, potion.getID());
    }

    @Test
    public void testSetters() {
        potion.setName("Poison");
        potion.setEffect(-10);
        potion.setID(999);

        assertEquals("Poison", potion.getName());
        assertEquals(-10, potion.getEffect());
        assertEquals(999, potion.getID());
    }
}