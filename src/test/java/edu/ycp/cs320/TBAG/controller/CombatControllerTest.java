package edu.ycp.cs320.TBAG.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.ycp.cs320.TBAG.model.Player;
import edu.ycp.cs320.TBAG.model.Monster;

public class CombatControllerTest {

    private CombatController combatController;
    private Player player;
    private Monster goblin; // Health damage monster
    private Monster ghost;  // Sanity damage monster

    @BeforeEach
    public void setUp() {
        combatController = new CombatController();

        // Set up a fresh player for every test
        player = new Player();
        player.setHealth(100);
        player.setSanity(100);
        player.setDamage(10); // Give the player 10 damage for easier math
        player.setRoomID(2);

        // Set up a standard health-damaging monster (20 HP, 5 DMG)
        goblin = new Monster("Goblin", 20, 5, 2, "health");

        // Set up a standard sanity-damaging monster (20 HP, 15 DMG)
        ghost = new Monster("Ghost", 20, 15, 2, "sanity");
    }

    @Test
    public void testPlayerKillsMonster() {
        // Boost player damage to ensure a one-hit kill
        player.setDamage(50);

        String result = combatController.attackTurn(player, goblin);

        // Monster health should drop below 0
        assertTrue(goblin.getHealth() <= 0);

        // Monster should be moved to room 0 (the "dead" room)
        assertEquals(0, goblin.getRoomID());

        // Player should NOT take damage because the monster died before it could swing back
        assertEquals(100, player.getHealth());

        // Check the combat log output
        assertTrue(result.contains("You defeated the Goblin!"));
    }

    @Test
    public void testNormalCombatExchangeHealthDamage() {
        // Player hits for 10, Goblin has 20 HP. Goblin hits for 5, Player has 100 HP.
        String result = combatController.attackTurn(player, goblin);

        // Check math
        assertEquals(10, goblin.getHealth());
        assertEquals(95, player.getHealth());
        assertEquals(100, player.getSanity()); // Sanity should be untouched

        // Check log
        assertTrue(result.contains("You hit the Goblin for 10 damage."));
        assertTrue(result.contains("The Goblin hits you for 5 damage!"));
        assertTrue(result.contains("What will you do next?"));
    }

    @Test
    public void testNormalCombatExchangeSanityDamage() {
        // Player hits for 10, Ghost has 20 HP. Ghost does 15 sanity damage.
        String result = combatController.attackTurn(player, ghost);

        // Check math
        assertEquals(10, ghost.getHealth());
        assertEquals(100, player.getHealth()); // Health should be untouched
        assertEquals(85, player.getSanity());

        // Check log
        assertTrue(result.contains("The Ghost drains your sanity by 15!"));
    }

    @Test
    public void testPlayerDiesFromHealthDamage() {
        // Make the goblin incredibly strong
        goblin.setDamage(200);

        String result = combatController.attackTurn(player, goblin);

        // Player health should drop below 0
        assertTrue(player.getHealth() <= 0);

        // Check log
        assertTrue(result.contains("You have died from your wounds..."));
    }

    @Test
    public void testPlayerGoesInsane() {
        // Make the ghost incredibly terrifying
        ghost.setDamage(200);

        String result = combatController.attackTurn(player, ghost);

        // Player sanity should drop below 0
        assertTrue(player.getSanity() <= 0);

        // Check log
        assertTrue(result.contains("You have completely lost your mind..."));
    }
}