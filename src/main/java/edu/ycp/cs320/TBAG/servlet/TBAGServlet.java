package edu.ycp.cs320.TBAG.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ycp.cs320.TBAG.controller.GameEngine;
import edu.ycp.cs320.TBAG.db.DatabaseProvider;
import edu.ycp.cs320.TBAG.db.IDatabase;
import edu.ycp.cs320.TBAG.model.Player;

public class TBAGServlet extends HttpServlet {
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

	private GameEngine initializeEngine(HttpSession session) {
		GameEngine engine = (GameEngine) session.getAttribute("engine");

		if (engine == null) {
			engine = new GameEngine();
			engine.setDatabase(db);

			Player player = db.findPlayer();
			engine.setPlayer(player);

			engine.loadMapFromDatabase();
			db.loadPlayerInventory(player);
			session.setAttribute("engine", engine);
		}

		return engine;
	}


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		HttpSession session = req.getSession();
		GameEngine engine = initializeEngine(session);

		req.setAttribute("player", engine.getPlayer());
		req.setAttribute("location", engine.getCurrentLocation());
		req.setAttribute("dialog", engine.getPlayer().getDialog());
		req.setAttribute("roomItems", engine.getRoomItems());

		req.getRequestDispatcher("/_view/tbag.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		HttpSession session = req.getSession();
		GameEngine engine = initializeEngine(session);

		String command = req.getParameter("command");
		String dialog = req.getParameter("dialog");

		if (dialog == null) {
			dialog = "";
		}
		if (command == null) {
			command = "";
		}

		String result = engine.processCommand(command);

		// Added a ">" so your typed commands stand out in the text log!
		dialog += "\n> " + command + "\n";
		dialog += result;

		// === THE FIX: PREVENT DATABASE CRASH ===
		// If the dialog log gets too massive, we chop off the oldest text at the top
		if (dialog.length() > 2800) {
			dialog = dialog.substring(dialog.length() - 2800);

			// We find the first newline character so we don't accidentally cut a word in half!
			int cutIndex = dialog.indexOf('\n');
			if (cutIndex != -1) {
				dialog = dialog.substring(cutIndex + 1);
			}
		}

		engine.getPlayer().setDialog(dialog);
		db.updatePlayer(engine.getPlayer());

		req.setAttribute("player", engine.getPlayer());
		req.setAttribute("location", engine.getCurrentLocation());
		req.setAttribute("dialog", dialog);
		req.setAttribute("roomItems", engine.getRoomItems());

		req.getRequestDispatcher("/_view/tbag.jsp").forward(req, resp);
	}
}