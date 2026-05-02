package edu.ycp.cs320.TBAG.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ycp.cs320.TBAG.controller.GameEngine;
import edu.ycp.cs320.TBAG.db.DatabaseProvider;
import edu.ycp.cs320.TBAG.db.IDatabase;
import edu.ycp.cs320.TBAG.model.Player;

public class IndexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.getRequestDispatcher("/_view/index.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String action = req.getParameter("action");

		// If the user clicked "New Game"
		if ("new".equals(action)) {
			try {
				IDatabase db = DatabaseProvider.getInstance();
				db.resetGame(); // Wipe the DB and generate a new random map

				// Clear the engine session so it's forced to load the fresh map
				req.getSession().removeAttribute("engine");

				// === NEW: Auto-look around the starting room! ===
				// We create a temporary mini-engine just to generate the starting text
				GameEngine initEngine = new GameEngine();
				initEngine.setDatabase(db);
				Player player = db.findPlayer();
				initEngine.setPlayer(player);
				initEngine.loadMapFromDatabase();

				// Run the "look" command silently behind the scenes
				String lookText = initEngine.processCommand("look");

				// Append the detailed room description to the opening dialog and save it
				player.setDialog(player.getDialog() + "\n" + lookText);
				db.updatePlayer(player);
				// ===============================================

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Redirect right into the game page
		resp.sendRedirect(req.getContextPath() + "/tbag");
	}
}