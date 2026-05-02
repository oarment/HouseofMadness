package edu.ycp.cs320.TBAG.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MonsterTest {
    private Monster ghost;

    @BeforeEach
    public void setUp() {
        // name, health, damage, roomID, damageType
        ghost = new Monster("Poltergeist", 30, 15, 2, "sanity");
    }

    @Test
    public void testMonsterAttributes() {
        assertEquals("Poltergeist", ghost.getName());
        assertEquals(30, ghost.getHealth());
        assertEquals(15, ghost.getDamage());
        assertEquals(2, ghost.getRoomID());
        assertEquals("sanity", ghost.getDamageType());
    }

    @Test
    public void testTakeDamage() {
        ghost.setHealth(ghost.getHealth() - 10);
        assertEquals(20, ghost.getHealth());
    }
}