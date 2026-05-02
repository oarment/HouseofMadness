package edu.ycp.cs320.TBAG.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlayerTest {
	private Player player;

	@BeforeEach
	public void setUp() {
		player = new Player();
	}

	@Test
	public void testDefaultConstructor() {
		assertEquals("Hero", player.getName());
		assertEquals(1, player.getRoomID());
		assertEquals(100, player.getHealth());
		assertEquals(100, player.getSanity());
		assertEquals(1, player.getDamage());
		assertNotNull(player.getInventory());
	}

	@Test
	public void testSanityAndHealthUpdates() {
		player.setHealth(50);
		player.setSanity(10);

		assertEquals(50, player.getHealth());
		assertEquals(10, player.getSanity());
	}

	@Test
	public void testDialogStorage() {
		player.setDialog("You entered the room.");
		assertEquals("You entered the room.", player.getDialog());
	}
}