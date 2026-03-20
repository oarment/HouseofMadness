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


		// call JSP to generate empty form
		req.getRequestDispatcher("/_view/tbag.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		System.out.println("TBAG Servlet: doPost");

		// create Player model
		HttpSession session = req.getSession();

		// Only create player if it doesn't exist
		Player player = (Player) session.getAttribute("player");

		if (player == null) {
			player = new Player();
			player.setHealth(100);
			player.setSanity(100);
			player.setRoomID(1);
			player.setDamage(1);

			session.setAttribute("player", player);
		}

		req.setAttribute("player", player);

		// Retrieve map
		List<Room> map = (List<Room>) session.getAttribute("map");


		if (map == null) {
			RoomController roomController = new RoomController();
			map = roomController.initializeMap();
			session.setAttribute("map", map);
		}

		// create GameEngine controller - controller does not persist between requests
		// must recreate it each time a Post comes in
		GameEngine engine = new GameEngine();

		// assign model reference to controller so that controller can access model
		engine.setPlayer(player);
		RoomController RoomController = new RoomController();

		//set the map up
		/*TODO - implement the initializemap() method
		- assign them to an arraylist
		- correctly update room location based on movement commands
		 */

		// Get running dialog text
		String dialog = req.getParameter("dialog");

		// get direction command from jsp
		String command = req.getParameter("command");
		// Append user's command
		dialog += command + "\n"; //adds command to return in text box on page
		// Attempt to move player
		String temp = "";
		String location = "";



		if (engine.movePlayer(command))
		{
			if(command.equals("north"))
			{
				temp = "You went north.\n";
				location = "Main Hall N";
			}
			if(command.equals("east"))
			{
				temp = "You went east.\n";
				location = "Library";
			}
			if(command.equals("west"))
			{
				temp = "You went west.\n";
				location = "Lounge";
			}
			if(command.equals("south"))
			{
				temp = "You went south.\n";
				location = "Main Hall S";
			}
			if(command.equals("jump"))
			{
				if(player.getHealth() < 100)
				{
					temp = "Stop jumping, you're hurting yourself.\n";
					player.setHealth(player.getHealth()-10);
				}
				else{
					temp = "You jumped and hit your head. -10hp\n";
					player.setHealth(player.getHealth()-10);
				}

			}
		}
		if (!engine.movePlayer(command)) {
			temp = "Sorry, command not recognized.\n";

		}
		dialog += temp;
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

		// now call the JSP to render the new page
		req.getRequestDispatcher("/_view/tbag.jsp").forward(req, resp);
	}

	// gets an Integer from the Posted form data, for the given attribute name
	private int getInteger(HttpServletRequest req, String name) {
		return Integer.parseInt(req.getParameter(name));
	}
}