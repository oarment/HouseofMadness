package edu.ycp.cs320.TBAG.db;

import edu.ycp.cs320.TBAG.db.InitialData.ItemSeed;
import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.Player;
import edu.ycp.cs320.TBAG.model.Room;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DerbyDatabase implements IDatabase {

	private interface Transaction<ResultType> {
		ResultType execute(Connection conn) throws SQLException;
	}

	private static final int MAX_ATTEMPTS = 10;

	public <ResultType> ResultType executeTransaction(Transaction<ResultType> txn) {
		try {
			return doExecuteTransaction(txn);
		} catch (SQLException e) {
			throw new PersistenceException("Transaction failed", e);
		}
	}

	public <ResultType> ResultType doExecuteTransaction(Transaction<ResultType> txn) throws SQLException {
		Connection conn = connect();

		try {
			int numAttempts = 0;
			boolean success = false;
			ResultType result = null;

			while (!success && numAttempts < MAX_ATTEMPTS) {
				try {
					result = txn.execute(conn);
					conn.commit();
					success = true;
				} catch (SQLException e) {
					if (e.getSQLState() != null && e.getSQLState().equals("41000")) {
						numAttempts++;
					} else {
						throw e;
					}
				}
			}

			if (!success) {
				throw new SQLException("Transaction failed (too many retries)");
			}

			return result;
		} finally {
			DBUtil.closeQuietly(conn);
		}
	}

	private Connection connect() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:derby:../tbag.db;create=true");
		conn.setAutoCommit(false);
		return conn;
	}

	@Override
	public void createTables() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt1 = null;
				PreparedStatement stmt2 = null;
				PreparedStatement stmt3 = null;

				try {
					stmt1 = conn.prepareStatement(
							"create table player (" +
									" player_id integer primary key, " +
									" health integer, " +
									" sanity integer, " +
									" damage integer, " +
									" room_id integer, " +
									" dialog varchar(20000)" +
									")"
					);
					stmt1.executeUpdate();
					System.out.println("Player table created");

					// === UPDATED SCHEMA FOR PUZZLES ===
					stmt2 = conn.prepareStatement(
							"create table rooms (" +
									" room_id integer primary key, " +
									" name varchar(100), " +
									" north_id integer, " +
									" south_id integer, " +
									" east_id integer, " +
									" west_id integer, " +
									" is_locked integer, " +
									" required_item varchar(100)" +
									")"
					);
					stmt2.executeUpdate();
					System.out.println("Rooms table created");

					stmt3 = conn.prepareStatement(
							"create table items (" +
									" item_id integer primary key, " +
									" name varchar(50), " +
									" type varchar(20), " +   // NEW
									" effect integer, " +
									" room_id integer" +
									")"
					);
					stmt3.executeUpdate();
					System.out.println("Items table created");

					return true;
				} finally {
					DBUtil.closeQuietly(stmt1);
					DBUtil.closeQuietly(stmt2);
					DBUtil.closeQuietly(stmt3);
				}
			}
		});
	}

	public void loadInitialData() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				Player player;
				List<Room> roomList;
				List<InitialData.ItemSeed> itemSeeds;

				try {
					player = InitialData.getPlayer();
					roomList = InitialData.getRooms();
					itemSeeds = InitialData.getItemSeeds();
				} catch (IOException e) {
					throw new SQLException("Couldn't read initial data", e);
				}

				PreparedStatement insertPlayer = null;
				PreparedStatement insertRoom = null;
				PreparedStatement insertItem = null;

				try {
					insertPlayer = conn.prepareStatement(
							"insert into player (player_id, health, sanity, damage, room_id, dialog) values (?, ?, ?, ?, ?, ?)"
					);
					insertPlayer.setInt(1, 1);
					insertPlayer.setInt(2, player.getHealth());
					insertPlayer.setInt(3, player.getSanity());
					insertPlayer.setInt(4, player.getDamage());
					insertPlayer.setInt(5, player.getRoomID());
					insertPlayer.setString(6, player.getDialog());
					insertPlayer.executeUpdate();
					System.out.println("Player table populated");


					// === UPDATED FOR LOCKS ===
					insertRoom = conn.prepareStatement(
							"insert into rooms (room_id, name, north_id, south_id, east_id, west_id, is_locked, required_item) values (?, ?, ?, ?, ?, ?, 0, '')"
					);
					for (Room room : roomList) {
						insertRoom.setInt(1, room.getRoomID());
						insertRoom.setString(2, room.getName());
						insertRoom.setInt(3, room.getNorth());
						insertRoom.setInt(4, room.getSouth());
						insertRoom.setInt(5, room.getEast());
						insertRoom.setInt(6, room.getWest());
						insertRoom.addBatch();
					}
					insertRoom.executeBatch();

					System.out.println("Rooms table populated");


					insertItem = conn.prepareStatement(
							"insert into items (item_id, name, type, effect, room_id) values (?, ?, ?, ?, ?)"
					);
					for (InitialData.ItemSeed item : itemSeeds) {
						insertItem.setInt(1, item.getId());
						insertItem.setString(2, item.getName());
						insertItem.setString(3, item.getType());
						insertItem.setInt(4, item.getEffect());
						insertItem.setInt(5, item.getRoomID());
						insertItem.addBatch();
					}
					insertItem.executeBatch();

					System.out.println("Items table populated");


					return true;
				} finally {
					DBUtil.closeQuietly(insertPlayer);
					DBUtil.closeQuietly(insertRoom);
					DBUtil.closeQuietly(insertItem);
				}
			}
		});
	}

	@Override
	public Player findPlayer() {
		return executeTransaction(new Transaction<Player>() {
			@Override
			public Player execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet rs = null;

				try {
					stmt = conn.prepareStatement("select * from player where player_id = 1");
					rs = stmt.executeQuery();

					if (rs.next()) {
						Player player = new Player();
						player.setHealth(rs.getInt("health"));
						player.setSanity(rs.getInt("sanity"));
						player.setDamage(rs.getInt("damage"));
						player.setRoomID(rs.getInt("room_id"));
						player.setDialog(rs.getString("dialog"));
						return player;
					}

					return null;
				} finally {
					DBUtil.closeQuietly(rs);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}

	@Override
	public boolean updatePlayer(final Player player) {
		return executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;

				try {
					stmt = conn.prepareStatement(
							"update player set health = ?, sanity = ?, damage = ?, room_id = ?, dialog = ? where player_id = 1"
					);
					stmt.setInt(1, player.getHealth());
					stmt.setInt(2, player.getSanity());
					stmt.setInt(3, player.getDamage());
					stmt.setInt(4, player.getRoomID());
					stmt.setString(5, player.getDialog());

					return stmt.executeUpdate() == 1;
				} finally {
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}



	@Override


	public void loadPlayerInventory(Player player) {

		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet rs = null;

				try {
					stmt = conn.prepareStatement("select * from items where room_id = -1 order by item_id asc");
					rs = stmt.executeQuery();

					while (rs.next()) {
						Item item = new Item(
								rs.getInt("item_id"),
								rs.getString("name"),
								rs.getString("type"),
								rs.getInt("effect"),
								rs.getInt("room_id")
						);
						player.getInventory().addItem(item);
					}

					return true;
				} finally {
					DBUtil.closeQuietly(rs);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}

	@Override
	public List<Room> findAllRooms() {
		return executeTransaction(new Transaction<List<Room>>() {
			@Override
			public List<Room> execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet rs = null;

				try {
					stmt = conn.prepareStatement("select * from rooms order by room_id asc");
					rs = stmt.executeQuery();

					List<Room> rooms = new ArrayList<Room>();
					while (rs.next()) {
						Room room = new Room(
								rs.getInt("room_id"),
								rs.getString("name"),
								rs.getInt("north_id"),
								rs.getInt("south_id"),
								rs.getInt("east_id"),
								rs.getInt("west_id")
						);
						rooms.add(room);
					}
					return rooms;
				} finally {
					DBUtil.closeQuietly(rs);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}

	@Override
	public List<Item> findAllItems() {
		return executeTransaction(new Transaction<List<Item>>() {
			@Override
			public List<Item> execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet rs = null;

				try {
					stmt = conn.prepareStatement("select * from items order by item_id asc");
					rs = stmt.executeQuery();

					List<Item> items = new ArrayList<Item>();

					while (rs.next()) {
						Item item = new Item(
								rs.getInt("item_id"),
								rs.getString("name"),
								rs.getString("type"),
								rs.getInt("effect"),
								rs.getInt("room_id")
						);
						items.add(item);
					}

					return items;
				} finally {
					DBUtil.closeQuietly(rs);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}

	@Override
	public List<Room> findFullMap() {
		return executeTransaction(new Transaction<List<Room>>() {
			@Override
			public List<Room> execute(Connection conn) throws SQLException {
				PreparedStatement roomStmt = null;
				PreparedStatement itemStmt = null;
				ResultSet roomRs = null;
				ResultSet itemRs = null;

				try {
					List<Room> rooms = new ArrayList<Room>();

					roomStmt = conn.prepareStatement("select * from rooms order by room_id asc");
					roomRs = roomStmt.executeQuery();

					while (roomRs.next()) {
						Room room = new Room(
								roomRs.getInt("room_id"),
								roomRs.getString("name"),
								roomRs.getInt("north_id"),
								roomRs.getInt("south_id"),
								roomRs.getInt("east_id"),
								roomRs.getInt("west_id")
						);
						// === PULL LOCKS FROM DATABASE ===
						room.setLocked(roomRs.getInt("is_locked") == 1);
						room.setRequiredItem(roomRs.getString("required_item"));

						rooms.add(room);
					}

					itemStmt = conn.prepareStatement("select * from items order by item_id asc");
					itemRs = itemStmt.executeQuery();

					while (itemRs.next()) {
						Item item = new Item(
								itemRs.getInt("item_id"),
								itemRs.getString("name"),
								itemRs.getString("type"),
								itemRs.getInt("effect"),
								itemRs.getInt("room_id")
						);

						if (item.getRoomID() > 0) {
							Room room = getRoomById(rooms, item.getRoomID());
							if (room != null) {
								room.getInventory().addItem(item);
							}
						}
					}

					return rooms;
				} finally {
					DBUtil.closeQuietly(roomRs);
					DBUtil.closeQuietly(itemRs);
					DBUtil.closeQuietly(roomStmt);
					DBUtil.closeQuietly(itemStmt);
				}
			}
		});
	}

	@Override
	public boolean updateItem(final Item item) {
		return executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;

				try {
					stmt = conn.prepareStatement(
							"update items set name = ?, type = ?, effect = ?, room_id = ? where item_id = ?"
					);
					stmt.setString(1, item.getName());
					stmt.setString(2, item.getType());
					stmt.setInt(3, item.getEffect());
					stmt.setInt(4, item.getRoomID());
					stmt.setInt(5, item.getID());

					return stmt.executeUpdate() == 1;
				} finally {
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}

	// ==========================================
	// === NEW GAME GENERATOR SYSTEM ===
	// ==========================================
	@Override
	public void resetGame() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement delPlayer = null;
				PreparedStatement delRooms = null;
				PreparedStatement delItems = null;
				PreparedStatement insertPlayer = null;
				PreparedStatement insertRoom = null;
				PreparedStatement insertItem = null;

				try {
					delPlayer = conn.prepareStatement("delete from player");
					delPlayer.executeUpdate();
					delRooms = conn.prepareStatement("delete from rooms");
					delRooms.executeUpdate();
					delItems = conn.prepareStatement("delete from items");
					delItems.executeUpdate();

					List<Room> newMap = edu.ycp.cs320.TBAG.controller.MapGenerator.generateRandomMap();
					edu.ycp.cs320.TBAG.controller.MapGenerator.populateItems(newMap);

					insertPlayer = conn.prepareStatement(
							"insert into player (player_id, health, sanity, damage, room_id, dialog) values (?, ?, ?, ?, ?, ?)"
					);
					insertPlayer.setInt(1, 1);
					insertPlayer.setInt(2, 100);
					insertPlayer.setInt(3, 100);
					insertPlayer.setInt(4, 10);
					insertPlayer.setInt(5, 1);
					insertPlayer.setString(6, "You awaken in a strange, shifting mansion...\n");
					insertPlayer.executeUpdate();

					insertRoom = conn.prepareStatement(
							"insert into rooms (room_id, name, north_id, south_id, east_id, west_id, is_locked, required_item) values (?, ?, ?, ?, ?, ?, ?, ?)"
					);
					for (Room room : newMap) {
						insertRoom.setInt(1, room.getRoomID());
						insertRoom.setString(2, room.getName());
						insertRoom.setInt(3, room.getNorth());
						insertRoom.setInt(4, room.getSouth());
						insertRoom.setInt(5, room.getEast());
						insertRoom.setInt(6, room.getWest());
						insertRoom.setInt(7, room.isLocked() ? 1 : 0);
						insertRoom.setString(8, room.getRequiredItem() == null ? "" : room.getRequiredItem());
						insertRoom.addBatch();
					}
					insertRoom.executeBatch();

					insertItem = conn.prepareStatement(
							"insert into items (item_id, name, type, effect, room_id) values (?, ?, ?, ?, ?)"
					);

					for (Room room : newMap) {
						for (Item item : room.getInventory().getItems()) {
							item.setRoomID(room.getRoomID());

							insertItem.setInt(1, item.getID());
							insertItem.setString(2, item.getName());
							insertItem.setString(3, item.getType());
							insertItem.setInt(4, item.getEffect());
							insertItem.setInt(5, item.getRoomID());
							insertItem.addBatch();
						}
					}
					insertItem.executeBatch();

					Item rustyKey = new Item(999, "Rusty Key", "key", 0, -3);
					insertItem.setInt(1, rustyKey.getID());
					insertItem.setString(2, rustyKey.getName());
					insertItem.setString(3, rustyKey.getType());
					insertItem.setInt(4, rustyKey.getEffect());
					insertItem.setInt(5, rustyKey.getRoomID());
					insertItem.executeUpdate();

					return true;
				} finally {
					DBUtil.closeQuietly(delPlayer);
					DBUtil.closeQuietly(delRooms);
					DBUtil.closeQuietly(delItems);
					DBUtil.closeQuietly(insertPlayer);
					DBUtil.closeQuietly(insertRoom);
					DBUtil.closeQuietly(insertItem);
				}
			}
		});
	}

	private Room getRoomById(List<Room> rooms, int id) {
		for (Room room : rooms) {
			if (room.getRoomID() == id) {
				return room;
			}
		}
		return null;
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Creating tables...");
		DerbyDatabase db = new DerbyDatabase();
		db.createTables();

		System.out.println("Loading initial data...");
		db.loadInitialData();

		System.out.println("TBAG DB successfully initialized!");
	}
}