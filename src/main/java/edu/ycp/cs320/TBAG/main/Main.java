package edu.ycp.cs320.TBAG.main;

import java.io.File;

import org.eclipse.jetty.server.Server;

import edu.ycp.cs320.TBAG.db.DatabaseProvider;
import edu.ycp.cs320.TBAG.db.DerbyDatabase;
import edu.ycp.cs320.TBAG.db.IDatabase;

public class Main {
	public static void main(String[] args) throws Exception {
		String webappCodeBase = "./war";
		File warFile = new File(webappCodeBase);
		Launcher launcher = new Launcher();

		// Initialize DB BEFORE Jetty starts
		IDatabase db = new DerbyDatabase();
		DatabaseProvider.setInstance(db);

		// get a server for port 8081
		System.out.println("CREATING: web server on port 8081");
		Server server = launcher.launch(true, 8081, warFile.getAbsolutePath(), "/tbag");

		System.out.println("STARTING: web server on port 8081");
		server.start();

		server.dumpStdErr();

		System.out.println("RUNNING: web server on port 8081");

		server.join();
	}
}