package org.vaadin.peter.contextmenu;

import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItemClickEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItemClickListener;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedListener;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedOnTableHeaderEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedOnTableRowEvent;

import com.vaadin.data.Item;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class ContextMenuUITest extends UI {

	private ContextMenuItemClickListener clickListener = new ContextMenuItemClickListener() {

		@Override
		public void contextMenuItemClicked(ContextMenuItemClickEvent event) {
			Notification.show(event.getSource().toString());
		}
	};

	private ContextMenuOpenedListener openListener = new ContextMenuOpenedListener() {

		@Override
		public void onContextMenuOpenFromRow(
				ContextMenuOpenedOnTableRowEvent event) {
			if (event.getPropertyId().equals("Name")) {
				event.getContextMenu().removeAllItems();
				event.getContextMenu().addItem("Change name");
			}

		}

		@Override
		public void onContextMenuOpenFromHeader(
				ContextMenuOpenedOnTableHeaderEvent event) {
			if (event.getPropertyId().equals("Name")) {
				event.getContextMenu().removeAllItems();
				event.getContextMenu().addItem("Update name column");
			}

		}
	};

	@Override
	protected void init(VaadinRequest request) {
		setSizeFull();

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();

		setContent(layout);

		final ContextMenu contextMenu = new ContextMenu();

		contextMenu.addItem("Test item #1").addItem("Child #1")
				.addItem("Child 2").addItemClickListener(clickListener);
		contextMenu.addItem("Test item #2");

		contextMenu.applyFor(layout);

		Table table = new Table();
		table.setWidth(500, Unit.PIXELS);
		table.setHeight(500, Unit.PIXELS);
		layout.addComponent(table);

		table.addContainerProperty("Name", String.class, null);
		table.addContainerProperty("Age", Integer.class, null);

		Item item = table.addItem(new Object());
		item.getItemProperty("Name").setValue("Peter");
		item.getItemProperty("Age").setValue(5);

		ContextMenu tableContextMenu = new ContextMenu();
		tableContextMenu.addContextMenuOpenListener(openListener);
		tableContextMenu.addItem("Table test item #1");
		tableContextMenu.applyForTableRows(table);

	}
}
