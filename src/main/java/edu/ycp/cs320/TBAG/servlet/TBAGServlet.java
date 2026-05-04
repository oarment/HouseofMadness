package edu.ycp.cs320.TBAG.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ycp.cs320.TBAG.controller.GameEngine;
import edu.ycp.cs320.TBAG.controller.RoomController;
import edu.ycp.cs320.TBAG.model.Player;
import edu.ycp.cs320.TBAG.model.Room;

public class TBAGServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        System.out.println("TBAG Servlet: doGet");

        HttpSession session = req.getSession();

        // Try to get engine from session
        GameEngine engine = (GameEngine) session.getAttribute("engine");

        // If first time, create everything
        if (engine == null) {
            engine = new GameEngine();

            // Initialize map
            RoomController rc = new RoomController();
            engine.setMap(rc.initializeMap());

            // Initialize player
            Player player = new Player();
            player.setHealth(100);
            player.setSanity(100);
            player.setRoomID(1);

            engine.setPlayer(player);

            // Save engine in session
            session.setAttribute("engine", engine);
        }

        // Get player + location
        Player player = engine.getPlayer();
        String location = engine.getCurrentLocation();

        // Initialize dialog if needed
        String dialog = "";

        // Send to JSP
        req.setAttribute("player", player);
        req.setAttribute("location", location);
        req.setAttribute("dialog", dialog);

        req.getRequestDispatcher("/_view/tbag.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        System.out.println("TBAG Servlet: doPost");

        // create Player model
        HttpSession session = req.getSession();

// Get engine from session
        GameEngine engine = (GameEngine) session.getAttribute("engine");

        if (engine == null) {
            engine = new GameEngine();

            RoomController rc = new RoomController();
            engine.setMap(rc.initializeMap());

            Player player = new Player();
            player.setHealth(100);
            player.setSanity(100);
            player.setRoomID(1);

            engine.setPlayer(player);

            session.setAttribute("engine", engine);
        }

// Get input
        String command = req.getParameter("command");
        String dialog = req.getParameter("dialog");

// Process command
        String result = engine.processCommand(command);

// Append dialog
        dialog += command + "\n";
        dialog += result;

// Get updated state
        String location = engine.getCurrentLocation();
        Player player = engine.getPlayer();

// Send to JSP
        req.setAttribute("location", location);
        req.setAttribute("dialog", dialog);
        req.setAttribute("player", player);

// Save engine back
        session.setAttribute("engine", engine);

        //I am commenting this line below so it does not crash
        //req.getRequestDispatcher("/_view/tbag.jsp").forward(req, resp);
        // get health and sanity of player
        String health = player.getHealth().toString();
        String sanity = player.getSanity().toString();


        // the JSP will display updated location name
        req.setAttribute("location", location);

        // the JSP will display updated dialog
        req.setAttribute("dialog", dialog);
        session.setAttribute("player", player);
        // the JSP will display updated health & sanity
        req.setAttribute("health", health);
        req.setAttribute("sanity", sanity);


        Room currentRoom = engine.getRoomById(player.getRoomID());
        String npcHint = currentRoom.getHint();
        req.setAttribute("npcHint", npcHint);

        // now call the JSP to render the new page
        req.getRequestDispatcher("/_view/tbag.jsp").forward(req, resp);
    }

    // gets an Integer from the Posted form data, for the given attribute name
    private int getInteger(HttpServletRequest req, String name) {
        return Integer.parseInt(req.getParameter(name));
    }
}
