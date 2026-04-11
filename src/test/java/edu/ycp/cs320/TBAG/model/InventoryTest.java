package edu.ycp.cs320.TBAG.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class InventoryTest {

    @Test
    public void testAddItem() {
        Inventory inv = new Inventory();
        Item item = new Item("Knife", "weapon", 3);

        inv.addItem(item);

        assertEquals(1, inv.getSize());
        assertFalse(inv.isEmpty());
    }

    @Test
    public void testRemoveItem() {
        Inventory inv = new Inventory();
        Item item = new Item("Knife", "weapon", 3);

        inv.addItem(item);
        inv.removeItem(item);

        assertEquals(0, inv.getSize());
        assertTrue(inv.isEmpty());
    }

    @Test
    public void testGetItems() {
        Inventory inv = new Inventory();
        Item item1 = new Item("Knife", "weapon", 3);
        Item item2 = new Item("Flashlight", "utility", 0);

        inv.addItem(item1);
        inv.addItem(item2);

        assertEquals(2, inv.getItems().size());
        assertTrue(inv.getItems().contains(item1));
        assertTrue(inv.getItems().contains(item2));
    }

    @Test
    public void testIsEmpty() {
        Inventory inv = new Inventory();

        assertTrue(inv.isEmpty());

        inv.addItem(new Item("Knife", "weapon", 3));

        assertFalse(inv.isEmpty());
    }
}