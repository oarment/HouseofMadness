/*package edu.ycp.cs320.TBAG.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {

    @Test
    public void testConstructorAndGetters() {
        Item item = new Item("Potion", "health", 20);

        assertEquals("Potion", item.getName());
        assertEquals("health", item.getType());
        assertEquals(20, item.getEffect());
    }

    @Test
    public void testSetName() {
        Item item = new Item("Potion", "health", 20);

        item.setName("Super Potion");

        assertEquals("Super Potion", item.getName());
    }

    @Test
    public void testSetEffect() {
        Item item = new Item("Potion", "health", 20);

        item.setEffect(50);

        assertEquals(50, item.getEffect());
    }

    @Test
    public void testDifferentItemTypes() {
        Item weapon = new Item("Knife", "weapon", 3);
        Item sanityItem = new Item("Pills", "sanity", 10);

        assertEquals("weapon", weapon.getType());
        assertEquals(3, weapon.getEffect());

        assertEquals("sanity", sanityItem.getType());
        assertEquals(10, sanityItem.getEffect());
    }
}
*/