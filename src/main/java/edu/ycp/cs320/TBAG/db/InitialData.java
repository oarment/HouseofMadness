package edu.ycp.cs320.TBAG.db;

import edu.ycp.cs320.TBAG.model.Player;
import edu.ycp.cs320.TBAG.model.Room;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InitialData {

	public static Player getPlayer() throws IOException {
		ReadCSV readPlayer = new ReadCSV("player.csv");
		try {
			List<String> tuple = readPlayer.next();
			if (tuple == null) {
				return null;
			}

			Iterator<String> i = tuple.iterator();
			Player player = new Player();

			Integer.parseInt(i.next()); // player_id
			player.setHealth(Integer.parseInt(i.next()));
			player.setSanity(Integer.parseInt(i.next()));
			player.setDamage(Integer.parseInt(i.next()));
			player.setRoomID(Integer.parseInt(i.next()));

			if (i.hasNext()) {
				player.setDialog(i.next());
			} else {
				player.setDialog("");
			}

			System.out.println("player loaded from CSV file");
			return player;
		} finally {
			readPlayer.close();
		}
	}

	public static List<Room> getRooms() throws IOException {
		List<Room> roomList = new ArrayList<Room>();
		ReadCSV readRooms = new ReadCSV("rooms.csv");
		try {
			while (true) {
				List<String> tuple = readRooms.next();
				if (tuple == null) {
					break;
				}

				Iterator<String> i = tuple.iterator();

				int roomID = Integer.parseInt(i.next());
				String name = i.next();
				int northID = Integer.parseInt(i.next());
				int southID = Integer.parseInt(i.next());
				int eastID = Integer.parseInt(i.next());
				int westID = Integer.parseInt(i.next());

				Room room = new Room(roomID, name, northID, southID, eastID, westID);
				roomList.add(room);
			}

			System.out.println("roomList loaded from CSV file");
			return roomList;
		} finally {
			readRooms.close();
		}
	}

	public static List<ItemSeed> getItemSeeds() throws IOException {
		List<ItemSeed> itemSeeds = new ArrayList<ItemSeed>();
		ReadCSV readItems = new ReadCSV("items.csv");
		try {
			while (true) {
				List<String> tuple = readItems.next();
				if (tuple == null) {
					break;
				}

				Iterator<String> i = tuple.iterator();

				String name = i.next();
				int effect = Integer.parseInt(i.next());
				int roomID = Integer.parseInt(i.next());

				itemSeeds.add(new ItemSeed(name, effect, roomID));
			}

			System.out.println("item seeds loaded from CSV file");
			return itemSeeds;
		} finally {
			readItems.close();
		}
	}

	public static class ItemSeed {
		private String name;
		private int effect;
		private int roomID;

		public ItemSeed(String name, int effect, int roomID) {
			this.name = name;
			this.effect = effect;
			this.roomID = roomID;
		}

		public String getName() {
			return name;
		}

		public int getEffect() {
			return effect;
		}

		public int getRoomID() {
			return roomID;
		}
	}
}