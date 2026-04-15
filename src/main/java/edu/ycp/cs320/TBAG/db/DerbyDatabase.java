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
									" dialog varchar(3000)" +
									")"
					);
					stmt1.executeUpdate();
					System.out.println("Player table created");

					stmt2 = conn.prepareStatement(
							"create table rooms (" +
									" room_id integer primary key, " +
									" name varchar(100), " +
									" north_id integer, " +
									" south_id integer, " +
									" east_id integer, " +
									" west_id integer" +
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

					insertRoom = conn.prepareStatement(
							"insert into rooms (room_id, name, north_id, south_id, east_id, west_id) values (?, ?, ?, ?, ?, ?)"
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
	public void loadPlayerInventory(final Player player) {
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

	private Room getRoomById(List<Room> rooms, int id) {
		for (Room room : rooms) {
			if (room.getRoomID() == id) {
				return room;
			}
		}
		return null;
	}
	private static class DriverShim implements Driver {
		private final Driver driver;

		DriverShim(Driver driver) {
			this.driver = driver;
		}

		@Override
		public Connection connect(String url, java.util.Properties info) throws SQLException {
			return driver.connect(url, info);
		}

		@Override
		public boolean acceptsURL(String url) throws SQLException {
			return driver.acceptsURL(url);
		}

		@Override
		public DriverPropertyInfo[] getPropertyInfo(String url, java.util.Properties info) throws SQLException {
			return driver.getPropertyInfo(url, info);
		}

		@Override
		public int getMajorVersion() {
			return driver.getMajorVersion();
		}

		@Override
		public int getMinorVersion() {
			return driver.getMinorVersion();
		}

		@Override
		public boolean jdbcCompliant() {
			return driver.jdbcCompliant();
		}

		@Override
		public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
			return driver.getParentLogger();
		}
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