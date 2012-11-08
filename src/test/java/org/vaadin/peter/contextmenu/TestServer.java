package org.vaadin.peter.contextmenu;

import java.io.File;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

public class TestServer {

	private static final int PORT = 9998;

	/**
	 * 
	 * Test server for the addon.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		startServer(PORT, ContextMenuUITest.class);
	}

	public static Server startServer(int port, Class<? extends UI> uiToTest)
			throws Exception {
		Server server = new Server();

		final Connector connector = new SelectChannelConnector();

		connector.setPort(port);
		server.setConnectors(new Connector[] { connector });

		WebAppContext context = new WebAppContext();

		VaadinAddonTestServlet servlet = new VaadinAddonTestServlet(uiToTest);

		ServletHolder servletHolder = new ServletHolder(servlet);
		servletHolder.setInitParameter("widgetset", "org.vaadin.peter.contextmenu.ContextmenuWidgetset");

		File file = new File("target/testwebapp");
		context.setWar(file.getPath());
		context.setContextPath("/");

		context.addServlet(servletHolder, "/*");
		server.setHandler(context);
		server.start();
		return server;
	}
}