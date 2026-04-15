package edu.ycp.cs320.TBAG.db;

import edu.ycp.cs320.TBAG.model.Item;
import edu.ycp.cs320.TBAG.model.Player;
import edu.ycp.cs320.TBAG.model.Room;

import java.util.List;

public interface IDatabase {
    Player findPlayer();
    boolean updatePlayer(Player player);

    List<Room> findAllRooms();
    List<Item> findAllItems();
    List<Room> findFullMap();
    void loadPlayerInventory(Player player);
    boolean updateItem(Item item);

    void createTables();
    void loadInitialData();
}