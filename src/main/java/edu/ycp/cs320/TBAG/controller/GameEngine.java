package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.Monster;
import edu.ycp.cs320.TBAG.model.Player;
import edu.ycp.cs320.TBAG.model.Room;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the TBAG game.
 */
public class GameEngine {

	private Player player;
	private List<Room> map;
	private List<Monster> monsters; // Add monster list
	private CombatController combatController; // Add combat controller

	public GameEngine() {
		this.monsters = new ArrayList<>();
		this.combatController = new CombatController();
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
	public void setMonsters(List<Monster> monsters) {
		this.monsters = monsters;
	}

	public Monster getMonsterInCurrentRoom() {
		for (Monster m : monsters) {
			if (m.getRoomID() == player.getRoomID() && m.getHealth() > 0) {
				return m;
			}
		}
		return null;
	}


	// Attempt to move player
	// Attempt to process a command
	public String processCommand(String command) {
		Room currentRoom = getRoomById(player.getRoomID());

		if (currentRoom == null) {
			return "Error: invalid room.\n";
		}

		command = command.toLowerCase().trim();

		// === COMBAT CHECK ===
		// Is there a living monster in this room?
		Monster monsterInRoom = getMonsterInCurrentRoom();
		boolean inCombat = (monsterInRoom != null);

		// If the player is in combat, restrict their actions!
		if (inCombat) {
			if (command.equals("attack") || command.equals("fight")) {
				// Process one turn of combat
				return combatController.attackTurn(player, monsterInRoom);
			} else {
				// Block movement and other commands
				return "You can't do that! A " + monsterInRoom.getName() + " blocks your path!\n(Type 'attack' to fight)\n";
			}
		}

		// === NORMAL COMMANDS (Only runs if NOT in combat) ===
		int nextRoomId = 0;
		String message = "";

		switch (command) {
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
				return message; // Early return for non-movement
			case "attack":
			case "fight":
				return "There is nothing here to attack.\n";
			default:
				return "Sorry, command not recognized.\n";
		}

		// Handle movement logic
		if (nextRoomId == 0) {
			return "You can't go that way.\n";
		}

		Room nextRoom = getRoomById(nextRoomId);
		if (nextRoom == null) {
			return "You can't go that way.\n";
		}

		// Move player
		player.setRoomID(nextRoomId);
		message += "You are now in the " + nextRoom.getName() + ".\n";

		// Check if they just walked into a room with a NEW monster
		Monster newMonsterInRoom = getMonsterInCurrentRoom();
		if (newMonsterInRoom != null) {
			message += "\nWatch out! A " + newMonsterInRoom.getName() + " is here! Combat started!\n";
		}

		return message;
	}

	public String getCurrentLocation() {
		Room room = getRoomById(player.getRoomID());
		return (room != null) ? room.getName() : "Unknown";
	}
}