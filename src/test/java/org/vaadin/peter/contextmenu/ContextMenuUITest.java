package org.vaadin.peter.contextmenu;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class ContextMenuUITest extends UI {

	@Override
	protected void init(VaadinRequest request) {
		setSizeFull();

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();

		setContent(layout);

		final ContextMenu contextMenu = new ContextMenu();
		contextMenu.extend(layout);

		contextMenu.addItem("Test item #1").addItem("Test Item #2");
	}
}
