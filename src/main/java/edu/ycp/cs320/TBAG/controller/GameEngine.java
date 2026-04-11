package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.Monster;
import edu.ycp.cs320.TBAG.model.Player;
import edu.ycp.cs320.TBAG.model.Room;
import edu.ycp.cs320.TBAG.model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the TBAG game.
 */
public class GameEngine {

	private Player player;
	private List<Room> map;

	// 1. ADDED THESE MISSING VARIABLES
	private List<Monster> monsters;
	private CombatController combatController;

	public GameEngine() {
		this.monsters = new ArrayList<>();
		this.combatController = new CombatController();
	}

	public void setPlayer(Player player) {
		this.player = player;
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

		String[] parts = command.toLowerCase().split(" ");
		String action = parts[0];

		// 2. ADDED COMBAT TRAP LOGIC
		Monster monsterInRoom = getMonsterInCurrentRoom();
		boolean inCombat = (monsterInRoom != null);

		// If the player is in combat, restrict their actions!
		if (inCombat) {
			if (action.equals("attack") || action.equals("fight")) {
                // Process one turn of combat
                return combatController.attackTurn(player, monsterInRoom);
            }
            else if (action.equals("use")) {
                return useItem(command);

			} else {
				// Block movement and other commands
				return "You can't do that! A " + monsterInRoom.getName() + " blocks your path!\n(Type 'attack' to fight)\n";
			}
		}

		switch (action) {
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

			case "pickup":
                if(parts.length < 2) {
                    return "Pick Up What?\n";
                }
                String itemName = command.substring(7); // handles "sanity pills"
				return pickUpItem(itemName);

			case "drop":
				return dropItemByName(command);

			case "show":
				if (command.equals("show inventory")) {
					return showInventory();
				}
				return "Invalid show command.\n";

            case "use":
                return useItem(command);
            case "unequip":
                player.setEquippedWeapon(null);
                player.setEquippedUtility(null);
                player.setDamage(1);
                return "You unequipped your item.\n";

			// 3. ADDED ATTACK COMMAND FOR EMPTY ROOMS
			case "attack":
			case "fight":
				return "There is nothing here to attack.\n";

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

		// 4. FIXED RETURN STATEMENT & ADDED AMBUSH ALERT
		message += "\n" + getRoomItems() + "\n";

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

	public String pickUpItem(String itemName) {
		Room room = getRoomById(player.getRoomID());

        for (Item item : room.getInventory().getItems()) {
            if(item.getName().equalsIgnoreCase(itemName)) {

                player.getInventory().addItem(item);
                room.getInventory().removeItem(item);

                return "You picked up: " + item.getName() + ".\n";
            }
        }
        return "That item is not here \n";
	}

	public String getRoomItems() {
		Room room = getRoomById(player.getRoomID());

		if( room.getInventory().getItems().isEmpty() ) {
			return "Items here: none";
		}

		String result = "Items here:\n";

		for(Item item : room.getInventory().getItems()) {
			result += "- " + item.getName() + ", ";
		}

		return result;
	}

	public String showInventory() {
		if( player.getInventory().getItems().isEmpty() ) {
			return "Your Inventory is empty\n";
		}

		String result = "Your Inventory:\n";
		for(Item item : player.getInventory().getItems()) {
			result += "- " + item.getName() + "\n ";
		}
		return result;
	}

	public String dropItemByName(String command) {
		Room room = getRoomById(player.getRoomID());

		if (player.getInventory().getItems().isEmpty()) {
			return "You don't have anything to drop.\n";
		}
		String[] parts = command.split(" ", 2);

		if (parts.length < 2) {
			return "Drop What?\n";
		}
		String itemName = parts[1].toLowerCase();

		for(Item item : player.getInventory().getItems()) {
			if (item.getName().toLowerCase().equals(itemName)) {

				player.getInventory().removeItem(item);
				room.getInventory().addItem(item);

				return "You dropped: \n" + item.getName() + ".\n";
			}
		}

		return "You don't have that item.\n";
	}

    public String useItem(String command) {
        if (player.getInventory().getItems().isEmpty()) {
            return "You don't have item to use.\n";
        }

        String[] parts = command.split(" ", 2);
        if (parts.length < 2) {
            return "Use What?\n";
        }

        String itemName = parts[1].trim();

        for (Item item : player.getInventory().getItems()) {

            if (item.getName().equalsIgnoreCase(itemName) && item.getEffect() > 0){
                switch (item.getType()) {
                    case "health":
                        player.setHealth(player.getHealth() + item.getEffect());
                        player.getInventory().removeItem(item);
                        return "You used " + item.getName() + " and gained " + item.getEffect() + " health.\n";
                    case "sanity":
                        player.setSanity(player.getSanity() + item.getEffect());
                        player.getInventory().removeItem(item);
                        return "You used " + item.getName() + " and gained " + item.getEffect() + " sanity.\n";
                    case "weapon":
                        player.setEquippedWeapon(item);
                        player.setDamage(player.getDamage() + item.getEffect());
                        return "You equipped " + item.getName() + ". Damage increased by " + item.getEffect() + ".\n";
                    case "utility":
                        player.setEquippedUtility(item);
                        return "You equipped " + item.getName() + ".\n";
                    default:
                        return "You can't use that item.\n";

                }
            }
        }
        return "You don't have that item.\n";
    }


}