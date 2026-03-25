package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.Player;
import edu.ycp.cs320.TBAG.model.Room;

import java.util.List;

/**
 * Controller for the TBAG game.
 */
public class GameEngine {

	private Player player;
	private List<Room> map;

	public GameEngine() {
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	public Player getPlayer() {return player;}
	public void setMap(List<Room> map) {
		this.map = map;
	}

	public Room getRoomById(int id) {
		for (Room room : map) {
			if (room.getRoomID() == id) {
				return room;
			}
		}
		return null;
	}
	// Attempt to move player
	public String processCommand(String command) {

		Room currentRoom = getRoomById(player.getRoomID());

		if (currentRoom == null) {
			return "Error: invalid room.\n";
		}

		int nextRoomId = 0;
		String message = "";

		switch (command.toLowerCase()) {
			case "north":
				nextRoomId = currentRoom.getNorth();
				message = "You went north.\n";
				break;

			case "south":
				nextRoomId = currentRoom.getSouth();
				message = "You went south.\n";
				break;

			case "east":
				nextRoomId = currentRoom.getEast();
				message = "You went east.\n";
				break;

			case "west":
				nextRoomId = currentRoom.getWest();
				message = "You went west.\n";
				break;

			case "jump":
				if (player.getHealth() < 100) {
					message = "Stop jumping, you're hurting yourself.\n";
				} else {
					message = "You jumped and hit your head. -10hp\n";
				}
				player.setHealth(player.getHealth() - 10);
				return message;

			default:
				return "Sorry, command not recognized.\n";
		}

		// Handle movement
		if (nextRoomId == 0) {
			return "You can't go that way.\n";
		}

		Room nextRoom = getRoomById(nextRoomId);

		if (nextRoom == null) {
			return "You can't go that way.\n";
		}

		// Move player
		player.setRoomID(nextRoomId);

		return message;
	}

	public String getCurrentLocation() {
		Room room = getRoomById(player.getRoomID());
		return (room != null) ? room.getName() : "Unknown";
	}




}