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
		contextMenu.addItem("Test item #1").addItem("Child #1")
				.addItem("Child 2");
		contextMenu.addItem("Test item #2");
		contextMenu.getState().showing = true;

		contextMenu.extend(layout);

	}
}
