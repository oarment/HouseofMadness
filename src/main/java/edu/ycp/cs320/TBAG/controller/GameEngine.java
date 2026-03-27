package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.Player;
import edu.ycp.cs320.TBAG.model.Room;
import edu.ycp.cs320.TBAG.model.Item;

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

        String[] parts = command.toLowerCase().split(" ");
        String action = parts[0];

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
                return pickUpItem(0);

            case "drop":
                return dropItemByName(command);

            case "show":
                if (command.equals("show inventory")) {
                    return showInventory();
                }
                return "Invalid show command.\n";

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

		return message + "\n" + getRoomItems() + "\n";
	}

	public String getCurrentLocation() {
		Room room = getRoomById(player.getRoomID());
		return (room != null) ? room.getName() : "Unknown";
	}

    public String pickUpItem(int itemID) {
        Room room = getRoomById(player.getRoomID());

        if (room.getInventory().getItems().isEmpty()) {
            return "There are no items here.\n";
        }

        var item = room.getInventory().getItems().get(itemID);

        player.getInventory().addItem(item);
        room.getInventory().removeItem(item);

        return " You picked up " + item.getName() + ".\n";
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
            return "Your Invnetory is empty\n";
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

                player .getInventory().removeItem(item);
                room.getInventory().addItem(item);

                return "You dropped: \n" + item.getName() + ".\n";
            }
        }

        return "You don't have that item.\n";
    }

}