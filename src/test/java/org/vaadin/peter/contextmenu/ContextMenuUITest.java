package org.vaadin.peter.contextmenu;

import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItemClickEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ItemClickListener;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class ContextMenuUITest extends UI {

	private ItemClickListener clickListener = new ItemClickListener() {

		@Override
		public void contextMenuItemClicked(ContextMenuItemClickEvent event) {
			Notification.show(event.getSource().toString());
		}
	};

	@Override
	protected void init(VaadinRequest request) {
		setSizeFull();

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();

		setContent(layout);

		final ContextMenu contextMenu = new ContextMenu();
		contextMenu.addItemClickListener(clickListener);

		contextMenu.addItem("Test item #1").addItem("Child #1")
				.addItem("Child 2");
		contextMenu.addItem("Test item #2");
		contextMenu.getState().showing = true;

		contextMenu.extend(layout);

	}
}
