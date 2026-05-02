package edu.ycp.cs320.TBAG.controller;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import edu.ycp.cs320.TBAG.model.Room;
import edu.ycp.cs320.TBAG.model.Item;

import java.util.Arrays;
import java.util.List;

public class MapGeneratorTest {

    @Test
    public void testProceduralGenerationSolvability() {
        for (int i = 0; i < 1000; i++) {
            List<Room> map = MapGenerator.generateRandomMap();
            MapGenerator.populateItems(map);

            // 1. Verify Room 4 is always the locked bottleneck
            Room lockedRoom = map.get(3); // Room ID 4 is index 3
            assertEquals(4, lockedRoom.getRoomID());
            assertTrue(lockedRoom.isLocked());
            assertEquals("rusty key", lockedRoom.getRequiredItem());

            // 2. Find where the Puzzle Box and Torn Note spawned
            int boxRoomId = -1;
            int noteRoomId = -1;
            for (Room room : map) {
                for (Item item : room.getInventory().getItems()) {
                    if (item.getName().equalsIgnoreCase("Puzzle Box")) boxRoomId = room.getRoomID();
                    if (item.getName().equalsIgnoreCase("Torn Note")) noteRoomId = room.getRoomID();
                }
            }

            // 3. Verify the items actually exist
            assertTrue(boxRoomId != -1, "The Puzzle Box didn't spawn in map iteration " + i + "!");
            assertTrue(noteRoomId != -1, "The Torn Note didn't spawn in map iteration " + i + "!");

            // 4. Verify they are in reachable/safe rooms before the locked door (1, 2, 3, 7, 8, or 9)
            List<Integer> safeRooms = Arrays.asList(1, 2, 3, 7, 8, 9);
            assertTrue(safeRooms.contains(boxRoomId),
                    "Map " + i + " is broken! Puzzle Box spawned in unreachable Room " + boxRoomId);
            assertTrue(safeRooms.contains(noteRoomId),
                    "Map " + i + " is broken! Torn Note spawned in unreachable Room " + noteRoomId);
        }
    }
}