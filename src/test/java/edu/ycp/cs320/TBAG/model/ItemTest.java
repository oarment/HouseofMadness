package edu.ycp.cs320.TBAG.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ItemTest {

    @Test
    public void testConstructorAndGetters() {
        Item item = new Item("Knife", "weapon", 3);

        assertEquals("Knife", item.getName());
        assertEquals("weapon", item.getType());
        assertEquals(3, item.getEffect());
    }

    @Test
    public void testSetters() {
        Item item = new Item("Knife", "weapon", 3);

        item.setName("Flashlight");
        item.setEffect(10);

        assertEquals("Flashlight", item.getName());
        assertEquals(10, item.getEffect());
    }

    @Test
    public void testEquippedWeapon() {
        Item weapon = new Item("Knife", "weapon", 3);
        Item item = new Item("Flashlight", "utility", 0);

        item.setEquippedWeapon(weapon);

        assertEquals(weapon, item.getEquippedWeapon());
    }

    @Test
    public void testEquippedUtility() {
        Item utility = new Item("Flashlight", "utility", 0);
        Item item = new Item("Knife", "weapon", 3);

        item.setEquippedUtility(utility);

        assertEquals(utility, item.getEquippedUtility());
    }
}