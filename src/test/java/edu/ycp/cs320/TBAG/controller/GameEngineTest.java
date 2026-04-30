package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.Room;
import edu.ycp.cs320.TBAG.model.Item;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import edu.ycp.cs320.TBAG.model.Player;

import edu.ycp.cs320.TBAG.db.IDatabase;
import java.util.ArrayList;
import java.util.List;

public class GameEngineTest {
	private GameEngine engine;
	private Player player;
	private Room startRoom;
	private Room lockedRoom;

	@BeforeEach
	public void setUp() {
		engine = new GameEngine();

		// 1. Setup a mini-map for testing
		List<Room> map = new ArrayList<>();

		startRoom = new Room(1, "Start Room", 2, 0, 0, 0);
		lockedRoom = new Room(2, "Locked Room", 0, 1, 0, 0);

		// Lock room 2 and require a "rusty key"
		lockedRoom.setLocked(true);
		lockedRoom.setRequiredItem("rusty key");

		map.add(startRoom);
		map.add(lockedRoom);
		engine.setMap(map);

		// 2. Put a key on the floor of the start room
		Item key = new Item("Rusty Key", 0, 100);
		startRoom.getInventory().addItem(key);

		// 3. Setup the Player
		player = new Player();
		player.setHealth(100);
		player.setSanity(100);
		player.setRoomID(1);
		engine.setPlayer(player);

		// 4. Create a dummy database that does nothing so the engine doesn't crash trying to save
		engine.setDatabase(new IDatabase() {
			public Player findPlayer() { return null; }
			public boolean updatePlayer(Player p) { return true; }
			public List<Room> findAllRooms() { return null; }
			public List<Item> findAllItems() { return null; }
			public List<Room> findFullMap() { return null; }
			public void loadPlayerInventory(Player p) {}
			public boolean updateItemLocation(int itemId, int roomId) { return true; }
			public void createTables() {}
			public void loadInitialData() {}

			// === ADDED THE MISSING METHOD HERE ===
			public void resetGame() {}
		});
	}

	@Test
	public void testLockedDoorBlocksPlayer() {
		// Try to move north without the key
		String response = engine.processCommand("north");

		// Player should still be in room 1
		assertTrue(player.getRoomID() == 1);
		assertTrue(response.contains("locked"));
	}

	@Test
	public void testUnlockAndMove() {
		// 1. Pick up the key (index 0 on the floor)
		engine.processCommand("pickup");

		// 2. Use the key
		String useResponse = engine.processCommand("use rusty key");
		assertTrue(useResponse.contains("unlock"));
		assertFalse(lockedRoom.isLocked()); // Room should now be unlocked internally

		// 3. Move north
		engine.processCommand("north");

		// Player should successfully be in room 2
		assertTrue(player.getRoomID() == 2);
	}

	@Test
	public void testInvalidMove() {
		engine.processCommand("south");

		// Room 1 south -> 0 (invalid), should NOT move
		assertTrue(player.getRoomID() == 1);
	}
}