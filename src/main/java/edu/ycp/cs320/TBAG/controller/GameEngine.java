package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.db.IDatabase;
import edu.ycp.cs320.TBAG.model.Monster;
import edu.ycp.cs320.TBAG.model.Player;
import edu.ycp.cs320.TBAG.model.Room;
import edu.ycp.cs320.TBAG.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameEngine {

	private Player player;
	private List<Room> map;
	private List<Monster> monsters;
	private CombatController combatController;
	private IDatabase db;
	private boolean quickTimeActive = false;
	private String quickTimePassword = "";
	private long quickTimeEndTime = 0;
	private int roomsVisited = 0;

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

	public boolean isGameOver() {
		return player.getHealth() <= 0 || player.getSanity() <= 0;
	}

	public Monster getMonsterInCurrentRoom() {
		for (Monster m : monsters) {
			if (m.getRoomID() == player.getRoomID() && m.getHealth() > 0) {
				return m;
			}
		}
		return null;
	}

	private Monster generateScaledMonster() {
		String[] healthMonsters = {"Vampire", "Zombie", "Werewolf"};
		String[] sanityMonsters = {"Ghost", "Witch", "Demon"};

		String type = (Math.random() > 0.5) ? "health" : "sanity";
		String randomName;

		if (type.equals("health")) {
			randomName = healthMonsters[(int)(Math.random() * healthMonsters.length)];
		} else {
			randomName = sanityMonsters[(int)(Math.random() * sanityMonsters.length)];
		}

		int scaledHealth = 15 + (roomsVisited + 4);
		int scaledDamage = 5 + ((roomsVisited / 2)+1);

		return new Monster(randomName, scaledHealth, scaledDamage, player.getRoomID(), type);
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
		if (command == null) {
			command = "";
		}

		command = command.trim();
		if (isGameOver()) {
			return "*** GAME OVER ***\nYour body and mind can no longer continue.\nType 'try again' to restart, or 'main menu' to return to the title screen.\n";
		}

		if (quickTimeActive) {
			return handleQuickTimeInput(command);
		}

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
				String combatResult = combatController.attackTurn(player, monsterInRoom);
				db.updatePlayer(player);

				if (isGameOver()) {
					return combatResult + "\n*** GAME OVER ***\nType 'try again' to restart, or 'main menu' to return to the title screen.\n";
				}

				return combatResult;
			} else {
				return "You can't do that! A " + monsterInRoom.getName() + " blocks your path!\n(Type 'attack' to fight)\n";
			}
		}

		switch (action) {
			case "help":
				return "Movement: 'north', 'south', 'east', 'west'.\n" +
						"Items: 'pickup', 'drop <item>', 'equip <weapon>', 'use <item>', 'show inventory'\n" +
						"Combat: 'attack'\n" +
						"Other: 'look', 'read <item>', 'open box with <code>'\n";
			case "north":
				nextRoomId = currentRoom.getNorth();
				message = "You went north.\n";
				roomsVisited++;
				break;
			case "south":
				nextRoomId = currentRoom.getSouth();
				message = "You went south.\n";
				roomsVisited++;
				break;
			case "east":
				nextRoomId = currentRoom.getEast();
				message = "You went east.\n";
				roomsVisited++;
				break;
			case "west":
				nextRoomId = currentRoom.getWest();
				message = "You went west.\n";
				roomsVisited++;
				break;
			case "pickup":
				return pickUpItem(0);
			case "drop":
				return dropItemByName(command);
				
			case "equip":
				return equipItemByName(command);

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
					puzzleBox.setRoomID(-2);
					db.updateItem(puzzleBox);
					Item rustyKey = null;
					for (Item item : db.findAllItems()) {
						if (item.getName().equalsIgnoreCase("Rusty Key")) {
							rustyKey = item;
							break;
						}
					}

					if (rustyKey != null) {
						player.getInventory().addItem(rustyKey);
						rustyKey.setRoomID(-1);
						db.updateItem(rustyKey);
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
		if (Math.random() < 0.05) {
			return message + startQuickTimeEvent();
		}

		if (nextRoomId == 6) {
			return "\n*** VICTORY! ***\nYou throw open the doors of the " + nextRoom.getName() + " and escape into the misty night! You survived The Hollow!\n\n(Click 'New Game' on the menu or type 'try again' to play a newly randomized map!)\n";
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


			monsters.add(generateScaledMonster());
		}

		Monster newMonsterInRoom = getMonsterInCurrentRoom();
		if (newMonsterInRoom != null) {
			message += "\nWatch out! A " + newMonsterInRoom.getName() + " is here! Combat started!\n";
		}

		return message;
	}

	public String getRoomDescription(Room room) {
		StringBuilder desc = new StringBuilder("--- " + room.getName() + " ---\n");

		if (room.getHint() != null && !room.getHint().equals("")) {
			desc.append(room.getHint()).append("\n");
		}

		return desc.toString();
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

		if (itemToUse.getType().equalsIgnoreCase("sanity")) {
			int newSanity = Math.min(100, player.getSanity() + itemToUse.getEffect());
			player.setSanity(newSanity);

			player.getInventory().removeItem(itemToUse);
			itemToUse.setRoomID(-999);

			db.updatePlayer(player);
			db.updateItem(itemToUse);

			return "You consumed the " + itemToUse.getName()
					+ " and restored " + itemToUse.getEffect()
					+ " sanity! Sanity is now " + player.getSanity() + ".\n";
		}

		if (itemToUse.getType().equalsIgnoreCase("health")) {
			int newHealth = Math.min(100, player.getHealth() + itemToUse.getEffect());
			player.setHealth(newHealth);

			player.getInventory().removeItem(itemToUse);
			itemToUse.setRoomID(-999);

			db.updatePlayer(player);
			db.updateItem(itemToUse);

			return "You consumed the " + itemToUse.getName()
					+ " and restored " + itemToUse.getEffect()
					+ " health! Health is now " + player.getHealth() + ".\n";
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


		item.setRoomID(-1);
		db.updateItem(item);



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
			result += "- " + item.getName() + "|" + item.getType() + "\n";
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


				item.setRoomID(player.getRoomID());
				db.updateItem(item);

				return "You dropped " + item.getName() + ".\n";
			}
		}
		return "You don't have that item.\n";
	}
	public String equipItemByName(String command) {
		if (player.getInventory().getItems().isEmpty()) {
			return "Your inventory is empty.\n";
		}

		String[] parts = command.split(" ", 2);
		if (parts.length < 2) {
			return "Equip what?\n";
		}

		String itemName = parts[1].toLowerCase();

		for (Item item : player.getInventory().getItems()) {
			if (item.getName().toLowerCase().equals(itemName)) {
				if (!item.getType().equalsIgnoreCase("weapon")) {
					return item.getName() + " is not a weapon.\n";
				}

				player.setDamage(item.getEffect());
				db.updatePlayer(player);

				return "You equipped " + item.getName() + ". Damage is now " + player.getDamage() + ".\n";
			}
		}

		return "You don't have that item.\n";
	}
	public int getCurrentMonsterHealth() {
		Monster monster = getMonsterInCurrentRoom();
		return monster != null ? monster.getHealth() : 0;
	}
	public int getCurrentMonsterMaxHealth() {
		return generateScaledMonster().getHealth();
	}

	public String useItemByName(String command) {
		if (player.getInventory().getItems().isEmpty()) {
			return "Your inventory is empty.\n";
		}

		String[] parts = command.split(" ", 2);
		if (parts.length < 2) {
			return "Use what?\n";
		}

		String itemName = parts[1].toLowerCase();

		for (Item item : player.getInventory().getItems()) {
			if (item.getName().toLowerCase().equals(itemName)) {

				if (item.getType().equalsIgnoreCase("health")) {
					player.setHealth(player.getHealth() + item.getEffect());
					player.getInventory().removeItem(item);

					// mark item as consumed
					item.setRoomID(-999);
					db.updatePlayer(player);
					db.updateItem(item);

					return "You used " + item.getName() + ". Health increased by "
							+ item.getEffect() + ".\n";
				}

				if (item.getType().equalsIgnoreCase("sanity")) {
					player.setSanity(player.getSanity() + item.getEffect());
					player.getInventory().removeItem(item);

					// mark item as consumed
					item.setRoomID(-999);
					db.updatePlayer(player);
					db.updateItem(item);

					return "You used " + item.getName() + ". Sanity increased by "
							+ item.getEffect() + ". Sanity is now " + player.getSanity() + ".\n";
				}

				return item.getName() + " cannot be used.\n";
			}
		}

		return "You don't have that item.\n";
	}
	public boolean canGoNorth() {
		Room room = getRoomById(player.getRoomID());
		return room != null && room.getNorth() != 0;
	}

	public boolean canGoSouth() {
		Room room = getRoomById(player.getRoomID());
		return room != null && room.getSouth() != 0;
	}

	public boolean canGoEast() {
		Room room = getRoomById(player.getRoomID());
		return room != null && room.getEast() != 0;
	}

	public boolean canGoWest() {
		Room room = getRoomById(player.getRoomID());
		return room != null && room.getWest() != 0;
	}

	public boolean isInCombat() {
		return getMonsterInCurrentRoom() != null;
	}

	public String getCurrentMonsterName() {
		Monster monster = getMonsterInCurrentRoom();
		return monster != null ? monster.getName() : "";
	}

	public String getCurrentMonsterImage() {
		Monster monster = getMonsterInCurrentRoom();

		if (monster == null) {
			return "";
		}

		return monster.getName().toLowerCase().replace(" ", "_") + ".png";
	}
	private String generateQuickTimePassword() {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		Random rand = new Random();
		StringBuilder password = new StringBuilder();

		for (int i = 0; i < 10; i++) {
			password.append(chars.charAt(rand.nextInt(chars.length())));
		}

		return password.toString();
	}

	private String startQuickTimeEvent() {
		quickTimeActive = true;
		quickTimePassword = generateQuickTimePassword();
		quickTimeEndTime = System.currentTimeMillis() + 10000;

		return "\nYou're frozen by fear! Type: " + quickTimePassword + "\n";
	}

	private String handleQuickTimeInput(String command) {
		long now = System.currentTimeMillis();

		if (now > quickTimeEndTime) {
			quickTimeActive = false;
			quickTimePassword = "";
			quickTimeEndTime = 0;

			player.setSanity(player.getSanity() - 15);
			db.updatePlayer(player);

			if (isGameOver()) {
				return "*** GAME OVER ***\nYour sanity has collapsed.\nType 'try again' to restart, or 'main menu' to return to the title screen.\n";
			}
			else {
				return "You failed to react in time. You lost 15 sanity.\n";
			}
		}

		if (command.equals(quickTimePassword)) {
			quickTimeActive = false;
			quickTimePassword = "";
			quickTimeEndTime = 0;

			return "You broke free from the fear!\n";
		}

		return "Wrong! Try again quickly. Type: " + quickTimePassword + "\n";
	}

}