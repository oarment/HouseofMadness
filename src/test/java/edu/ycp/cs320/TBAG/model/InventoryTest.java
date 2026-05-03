package edu.ycp.cs320.TBAG.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InventoryTest {
    private Inventory inventory;
    private Item key;
    private Item potion;

    @BeforeEach
    public void setUp() {
        inventory = new Inventory();
        key = new Item(1, "Rusty Key", "key", 0, -1);
        potion = new Item(2, "Health Potion", "health", 25, -1);
    }

    @Test
    public void testAddAndSize() {
        assertTrue(inventory.isEmpty());

        inventory.addItem(key);
        inventory.addItem(potion);

        assertFalse(inventory.isEmpty());
        assertEquals(2, inventory.getSize());
        assertTrue(inventory.getItems().contains(key));
    }

    @Test
    public void testRemoveItem() {
        inventory.addItem(key);
        inventory.addItem(potion);

        inventory.removeItem(key);

        assertEquals(1, inventory.getSize());
        assertFalse(inventory.getItems().contains(key));
        assertTrue(inventory.getItems().contains(potion));
    }
}