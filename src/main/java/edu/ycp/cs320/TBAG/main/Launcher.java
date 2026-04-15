package edu.ycp.cs320.TBAG.main;

import java.io.File;
import java.lang.management.ManagementFactory;

import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

public class Launcher {
	public Server launch(boolean fromEclipse, int port, String warUrl, String contextPath) throws Exception {
		Server server = new Server(port);

		MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
		server.addBean(mbContainer);

		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath(contextPath);
		webapp.setWar(warUrl);

		// IMPORTANT: use parent-first classloading so Jetty can see Derby
		webapp.setParentLoaderPriority(true);

		onCreateWebAppContext(webapp);

		Configuration.ClassList classList = Configuration.ClassList.setServerDefault(server);
		classList.addBefore(
				"org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
				"org.eclipse.jetty.annotations.AnnotationConfiguration");

		webapp.setAttribute(
				"org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
				".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$");

		webapp.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
		webapp.setInitParameter("org.eclipse.jetty.servlet.Default.welcomeServlets", "true");

		if (fromEclipse) {
			String extraClasspath =
					new File("build/classes/java/main").getAbsolutePath() + ";" +
							new File("build/resources/main").getAbsolutePath() + ";" +
							new File("war/WEB-INF/lib/derby.jar").getAbsolutePath();

			webapp.setExtraClasspath(extraClasspath);
		}

		server.setHandler(webapp);
		return server;
	}

	protected void onCreateWebAppContext(WebAppContext webapp) {
		// Does nothing by default, subclasses may override
	}
}