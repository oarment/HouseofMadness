/*package edu.ycp.cs320.TBAG.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest {

    @Test
    public void testAddItem() {
        Inventory inv = new Inventory();
        Item item = new Item("Potion", "health", 20);

        inv.addItem(item);

        assertEquals(1, inv.getSize());
        assertEquals("Potion", inv.getItems().get(0).getName());
    }

    @Test
    public void testRemoveItem() {
        Inventory inv = new Inventory();
        Item item = new Item("Potion", "health", 20);

        inv.addItem(item);
        inv.removeItem(item);

        assertTrue(inv.isEmpty());
    }

    @Test
    public void testIsEmpty() {
        Inventory inv = new Inventory();

        assertTrue(inv.isEmpty());

        inv.addItem(new Item("Knife", "weapon", 3));

        assertFalse(inv.isEmpty());
    }
}
*/