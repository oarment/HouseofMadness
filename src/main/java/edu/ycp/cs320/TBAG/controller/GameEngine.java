package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.db.IDatabase;
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

	private List<Monster> monsters;
	private CombatController combatController;

	private IDatabase db;

	public GameEngine() {
		this.monsters = new ArrayList<>();
		this.combatController = new CombatController();
	}

	public void setDatabase(IDatabase db) {
		this.db = db;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public void setMonsters(List<Monster> monsters) {
		this.monsters = monsters;
	}

	public void setMap(List<Room> map) {
		this.map = map;
	}

	public List<Room> getMap() {
		return map;
	}

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

		String[] parts = command.toLowerCase().split(" ");
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

			case "equip":
				return equipItemByName(command);

			case "use":
				return useItemByName(command);

			case "show":
				if (command.equals("show inventory")) {
					return showInventory();
				}
				return "Invalid show command.\n";

			case "attack":
			case "fight":
				return "There is nothing here to attack.\n";

			default:
				return "Sorry, command not recognized.\n";
		}

		if (nextRoomId == 0) {
			return "You can't go that way.\n";
		}

		Room nextRoom = getRoomById(nextRoomId);

		if (nextRoom == null) {
			return "You can't go that way.\n";
		}

		player.setRoomID(nextRoomId);
		db.updatePlayer(player);

		message += "\n" + getRoomItems() + "\n";

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

	public String pickUpItem(int itemIndex) {
		Room room = getRoomById(player.getRoomID());

		if (room.getInventory().getItems().isEmpty()) {
			return "There are no items here.\n";
		}

		Item item = room.getInventory().getItems().get(itemIndex);

		player.getInventory().addItem(item);
		room.getInventory().removeItem(item);

		item.setRoomID(-1);
		db.updateItem(item);

		return "You picked up " + item.getName() + ".\n";
	}

	public String getRoomItems() {
		Room room = getRoomById(player.getRoomID());

		if (room.getInventory().getItems().isEmpty()) {
			return "Items here: none";
		}

		String result = "Items here:\n";

		for (Item item : room.getInventory().getItems()) {
			result += "- " + item.getName() + "\n";
		}

		return result;
	}

	public String showInventory() {
		if (player.getInventory().getItems().isEmpty()) {
			return "Your Inventory is empty\n";
		}

		String result = "Your Inventory:\n";
		for (Item item : player.getInventory().getItems()) {
			result += "- " + item.getName() + " [" + item.getType() + ", " + item.getEffect() + "]\n";
		}
		result += "Current damage: " + player.getDamage() + "\n";
		return result;
	}

	public String dropItemByName(String command) {
		Room room = getRoomById(player.getRoomID());

		if (player.getInventory().getItems().isEmpty()) {
			return "You don't have anything to drop.\n";
		}

		String[] parts = command.split(" ", 2);
		if (parts.length < 2) {
			return "Drop what?\n";
		}

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

}