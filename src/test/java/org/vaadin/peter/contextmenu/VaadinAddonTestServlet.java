package org.vaadin.peter.contextmenu;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

public class VaadinAddonTestServlet extends VaadinServlet {

	private Class<? extends UI> uiToTest;

	public VaadinAddonTestServlet(Class<? extends UI> uiToTest) {
		this.uiToTest = uiToTest;

	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		getService().addSessionInitListener(new SessionInitListener() {
			@Override
			public void sessionInit(SessionInitEvent event)
					throws ServiceException {
				event.getSession().addUIProvider(new AddonTestUIProvider());
			}
		});
	}

	private class AddonTestUIProvider extends UIProvider {

		@Override
		public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
			String requestPathInfo = event.getRequest().getRequestPathInfo();

			if (requestPathInfo != null && !requestPathInfo.isEmpty()) {
				return uiToTest;
			}

			return null;
		}
	}
}
