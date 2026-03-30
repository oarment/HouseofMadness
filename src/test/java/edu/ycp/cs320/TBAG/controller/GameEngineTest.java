package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.Room;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import edu.ycp.cs320.TBAG.controller.GameEngine;
import edu.ycp.cs320.TBAG.model.Player;

import java.util.List;

public class GameEngineTest {
	private GameEngine engine;
	private Player player;

	@BeforeEach
	public void setUp() {
		engine = new GameEngine();

		RoomController rc = new RoomController();
		List<Room> map = rc.initializeMap();
		engine.setMap(map);

		player = new Player();
		player.setHealth(100);
		player.setSanity(100);
		player.setRoomID(1);

		engine.setPlayer(player);
	}

	@Test
	public void testMoveNorth() {
		engine.processCommand("north");

		// Room 1 north -> room 4
		assertTrue(player.getRoomID() == 4);
	}

	@Test
	public void testMoveWest() {
		engine.processCommand("west");

		// Room 1 west -> room 2
		assertTrue(player.getRoomID() == 2);
	}

	@Test
	public void testInvalidMove() {
		engine.processCommand("south");

		// Room 1 south -> 0 (invalid), should NOT move
		assertTrue(player.getRoomID() == 1);
	}



}
