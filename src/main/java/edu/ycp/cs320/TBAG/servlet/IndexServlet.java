package edu.ycp.cs320.TBAG.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ycp.cs320.TBAG.controller.GameEngine;
import edu.ycp.cs320.TBAG.db.DatabaseProvider;
import edu.ycp.cs320.TBAG.db.IDatabase;
import edu.ycp.cs320.TBAG.model.Player;
import edu.ycp.cs320.TBAG.model.Room;

public class IndexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private IDatabase db;

	@Override
	public void init() throws ServletException {
		try {
			db = DatabaseProvider.getInstance();
		} catch (Exception e) {
			throw new ServletException("Failed to initialize database", e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Player player = db.findPlayer();
		List<Room> map = db.findFullMap();

		Room lastRoom = null;
		for (Room room : map) {
			if (room.getRoomID() == player.getRoomID()) {
				lastRoom = room;
				break;
			}
		}

		String roomName = lastRoom != null ? lastRoom.getName() : "Unknown";
		String roomImage = roomName.toLowerCase().replaceAll("[^a-z0-9]+", "_") + ".png";

		req.setAttribute("lastRoomName", roomName);
		req.setAttribute("lastRoomImage", roomImage);

		req.getRequestDispatcher("/_view/index.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String action = req.getParameter("action");

		if ("new".equals(action)) {
			try {
				db.resetGame();

				req.getSession().removeAttribute("engine");

				GameEngine initEngine = new GameEngine();
				initEngine.setDatabase(db);

				Player player = db.findPlayer();
				initEngine.setPlayer(player);
				initEngine.loadMapFromDatabase();

				String lookText = initEngine.processCommand("look");

				player.setDialog(player.getDialog() + "\n" + lookText);
				db.updatePlayer(player);

			} catch (Exception e) {
				throw new ServletException("Failed to reset game", e);
			}
		}

		resp.sendRedirect(req.getContextPath() + "/tbag");
	}
}