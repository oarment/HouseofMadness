package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.db.IDatabase;
import edu.ycp.cs320.TBAG.model.Monster;
import edu.ycp.cs320.TBAG.model.Player;
import edu.ycp.cs320.TBAG.model.Room;
import edu.ycp.cs320.TBAG.model.Item;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {

	private Player player;
	private List<Room> map;
	private List<Monster> monsters;
	private CombatController combatController;
	private IDatabase db;

	public GameEngine() {
		this.monsters = new ArrayList<>();
		this.combatController = new CombatController();
	}

	public void setDatabase(IDatabase db) { this.db = db; }
	public void setPlayer(Player player) { this.player = player; }
	public Player getPlayer() { return player; }
	public void setMonsters(List<Monster> monsters) { this.monsters = monsters; }
	public void setMap(List<Room> map) { this.map = map; }
	public List<Room> getMap() { return map; }

	public void loadMapFromDatabase() {
		this.map = db.findFullMap();
	}

	public Monster getMonsterInCurrentRoom() {
		for (Monster m : monsters) {
			if (m.getRoomID() == player.getRoomID() && m.getHealth() > 0) {
				return m;
			}
		}
		return null;
	}

	public Room getRoomById(int id) {
		for (Room room : map) {
			if (room.getRoomID() == id) {
				return room;
			}
		}
		return null;
	}

	public String processCommand(String command) {
		Room currentRoom = getRoomById(player.getRoomID());

		if (currentRoom == null) {
			return "Error: invalid room.\n";
		}

		int nextRoomId = 0;
		String message = "";

		String[] parts = command.toLowerCase().split(" ", 2);
		String action = parts[0];

		Monster monsterInRoom = getMonsterInCurrentRoom();
		boolean inCombat = (monsterInRoom != null);

		if (inCombat) {
			if (action.equals("attack") || action.equals("fight")) {
				return combatController.attackTurn(player, monsterInRoom);
			} else {
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
				db.updatePlayer(player);
				return message;
			case "pickup":
				return pickUpItem(0);
			case "drop":
				return dropItemByName(command);
			case "show":
				if (command.equals("show inventory")) {
					return showInventory();
				}
				return "Invalid show command.\n";
			case "use":
				if (parts.length < 2) return "Use what?\n";
				return useItem(parts[1].trim(), currentRoom);
			case "look":
				return getRoomDescription(currentRoom) + "\n" + getRoomItems() + "\n";
			case "spawn":
				Monster cheatMonster = new Monster("Zombie", 15, 5, player.getRoomID(), "health");
				monsters.add(cheatMonster);
				return "DEV COMMAND: You summoned a Zombie!\nWatch out! A Zombie is here! Combat started!\n";
			case "attack":
			case "fight":
				return "There is nothing here to attack.\n";
			case "read":
				if (parts.length < 2) return "Read what?\n";
				if (parts[1].contains("note")) {
					for (Item item : player.getInventory().getItems()) {
						if (item.getName().equalsIgnoreCase("Torn Note")) {
							return "You unfold the Torn Note. Scrawled in dark ink is a message:\n'They are coming. The code to the box is " + item.getEffect() + ".'\n";
						}
					}
					return "You don't have a note to read.\n";
				}
				return "You can't read that.\n";
			case "open":
			case "unlock":
				if (parts.length < 2 || !parts[1].contains("box")) {
					return "What are you trying to open?\n";
				}

				Item puzzleBox = null;
				for (Item item : player.getInventory().getItems()) {
					if (item.getName().equalsIgnoreCase("Puzzle Box")) {
						puzzleBox = item;
						break;
					}
				}
				if (puzzleBox == null) return "You don't have a Puzzle Box in your inventory.\n";

				String enteredCode = command.replaceAll("[^0-9]", "");
				if (enteredCode.isEmpty()) return "The box has a 4-digit combination lock. You need to enter a code (e.g., 'open box with 1234').\n";

				int actualCode = -1;
				for (Item item : db.findAllItems()) {
					if (item.getName().equalsIgnoreCase("Torn Note")) {
						actualCode = item.getEffect();
						break;
					}
				}

				if (enteredCode.equals(String.valueOf(actualCode))) {
					player.getInventory().removeItem(puzzleBox);
					db.updateItemLocation(puzzleBox.getID(), -2);

					Item rustyKey = null;
					for (Item item : db.findAllItems()) {
						if (item.getName().equalsIgnoreCase("Rusty Key")) {
							rustyKey = item;
							break;
						}
					}

					if (rustyKey != null) {
						player.getInventory().addItem(rustyKey);
						db.updateItemLocation(rustyKey.getID(), -1);
						return "CLICK! The combination works! The box springs open, revealing a Rusty Key inside! You throw the empty box away.\n";
					}
				} else {
					return "You try combination " + enteredCode + "... but the box remains firmly locked.\n";
				}
				break;

			default:
				return "Sorry, command not recognized.\n";
		}

		if (nextRoomId == 0) return "You can't go that way.\n";

		Room nextRoom = getRoomById(nextRoomId);
		if (nextRoom == null) return "You can't go that way.\n";

		if (nextRoom.isLocked()) {
			return "The door to the " + nextRoom.getName() + " is locked! You need a " + nextRoom.getRequiredItem() + " to open it.\n";
		}

		player.setRoomID(nextRoomId);
		db.updatePlayer(player);

		if (nextRoomId == 6) {
			return "\n*** VICTORY! ***\nYou throw open the doors of the " + nextRoom.getName() + " and escape into the misty night! You survived The Hollow!\n\n(Click 'New Game' on the menu to play a newly randomized map)\n";
		}

		message += "\n" + getRoomDescription(nextRoom) + "\n" + getRoomItems() + "\n";

		// === 30% CHANCE & NEW MONSTER LOGIC ===
		if (Math.random() < 0.30) {
			String[] healthMonsters = {"Vampire", "Zombie", "Werewolf"};
			String[] sanityMonsters = {"Ghost", "Witch", "Demon"};

			String randomName;
			String type = (Math.random() > 0.5) ? "health" : "sanity";

			if (type.equals("health")) {
				randomName = healthMonsters[(int)(Math.random() * healthMonsters.length)];
			} else {
				randomName = sanityMonsters[(int)(Math.random() * sanityMonsters.length)];
			}

			Monster randomMonster = new Monster(randomName, 15, 5, player.getRoomID(), type);
			monsters.add(randomMonster);
		}

		Monster newMonsterInRoom = getMonsterInCurrentRoom();
		if (newMonsterInRoom != null) {
			message += "\nWatch out! A " + newMonsterInRoom.getName() + " is here! Combat started!\n";
		}

		return message;
	}

	public String getRoomDescription(Room room) {
		StringBuilder desc = new StringBuilder("--- " + room.getName() + " ---\n");
		desc.append(room.getHint()).append("\n\n");
		if (room.getNorth() != 0) desc.append(getDoorDescription("North", room.getNorth()));
		if (room.getSouth() != 0) desc.append(getDoorDescription("South", room.getSouth()));
		if (room.getEast() != 0) desc.append(getDoorDescription("East", room.getEast()));
		if (room.getWest() != 0) desc.append(getDoorDescription("West", room.getWest()));
		return desc.toString();
	}

	private String getDoorDescription(String direction, int roomId) {
		Room adj = getRoomById(roomId);
		if (adj != null && adj.isLocked()) {
			return "To the " + direction + ", there is a heavy door locked requiring a " + adj.getRequiredItem() + ".\n";
		} else if (adj != null) {
			return "There is a pathway to the " + direction + ".\n";
		}
		return "";
	}

	public String useItem(String itemName, Room currentRoom) {
		boolean hasItem = false;
		Item itemToUse = null;

		for (Item item : player.getInventory().getItems()) {
			if (item.getName().toLowerCase().equals(itemName)) {
				hasItem = true;
				itemToUse = item;
				break;
			}
		}

		if (!hasItem) return "You don't have a " + itemName + ".\n";

		int[] adjacentIds = {currentRoom.getNorth(), currentRoom.getSouth(), currentRoom.getEast(), currentRoom.getWest()};
		for (int id : adjacentIds) {
			if (id != 0) {
				Room adj = getRoomById(id);
				if (adj != null && adj.isLocked() && adj.getRequiredItem().toLowerCase().equals(itemName)) {
					adj.setLocked(false);
					return "You used the " + itemToUse.getName() + " to unlock the " + adj.getName() + "!\n";
				}
			}
		}

		if (itemToUse.getEffect() > 0) {
			String itemNameLower = itemToUse.getName().toLowerCase();
			if (itemNameLower.contains("sanity") || itemNameLower.contains("pill")) {
				int newSanity = player.getSanity() + itemToUse.getEffect();
				if (newSanity > 100) newSanity = 100;
				player.setSanity(newSanity);
				player.getInventory().removeItem(itemToUse);
				db.updateItemLocation(itemToUse.getID(), -2);
				return "You consumed the " + itemToUse.getName() + " and restored " + itemToUse.getEffect() + " sanity!\n";
			} else {
				int newHealth = player.getHealth() + itemToUse.getEffect();
				if (newHealth > 100) newHealth = 100;
				player.setHealth(newHealth);
				player.getInventory().removeItem(itemToUse);
				db.updateItemLocation(itemToUse.getID(), -2);
				return "You consumed the " + itemToUse.getName() + " and restored " + itemToUse.getEffect() + " health!\n";
			}
		}

		return "You can't use that here.\n";
	}

	public String getCurrentLocation() {
		Room room = getRoomById(player.getRoomID());
		return (room != null) ? room.getName() : "Unknown";
	}

	public String pickUpItem(int itemIndex) {
		Room room = getRoomById(player.getRoomID());
		if (room.getInventory().getItems().isEmpty()) return "There are no items here.\n";

		Item item = room.getInventory().getItems().get(itemIndex);
		player.getInventory().addItem(item);
		room.getInventory().removeItem(item);
		db.updateItemLocation(item.getID(), -1);

		return "You picked up " + item.getName() + ".\n";
	}

	public String getRoomItems() {
		Room room = getRoomById(player.getRoomID());
		if (room.getInventory().getItems().isEmpty()) return "Items here: none";

		String result = "Items here:\n";
		for (Item item : room.getInventory().getItems()) {
			result += "- " + item.getName() + "\n";
		}
		return result;
	}

	public String showInventory() {
		if (player.getInventory().getItems().isEmpty()) return "Your Inventory is empty\n";

		String result = "Your Inventory:\n";
		for (Item item : player.getInventory().getItems()) {
			result += "- " + item.getName() + "\n";
		}
		return result;
	}

	public String dropItemByName(String command) {
		Room room = getRoomById(player.getRoomID());
		if (player.getInventory().getItems().isEmpty()) return "You don't have anything to drop.\n";

		String[] parts = command.split(" ", 2);
		if (parts.length < 2) return "Drop what?\n";

		String itemName = parts[1].toLowerCase();
		for (Item item : player.getInventory().getItems()) {
			if (item.getName().toLowerCase().equals(itemName)) {
				player.getInventory().removeItem(item);
				room.getInventory().addItem(item);
				db.updateItemLocation(item.getID(), player.getRoomID());
				return "You dropped " + item.getName() + ".\n";
			}
		}
		return "You don't have that item.\n";
	}
}