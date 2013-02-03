package org.vaadin.peter.contextmenu;

import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItemClickEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItemClickListener;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedListener;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedListener.ComponentListener;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedOnComponentEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedOnTableFooterEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedOnTableHeaderEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedOnTableRowEvent;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Item;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("test")
public class ContextMenuApplication extends UI {

	private ContextMenuItemClickListener clickListener = new ContextMenuItemClickListener() {

		@Override
		public void contextMenuItemClicked(ContextMenuItemClickEvent event) {
			Notification.show(event.getSource().toString());
		}
	};

	private ContextMenuOpenedListener.TableListener openListener = new ContextMenuOpenedListener.TableListener() {

		@Override
		public void onContextMenuOpenFromRow(
				ContextMenuOpenedOnTableRowEvent event) {

		}

		@Override
		public void onContextMenuOpenFromHeader(
				ContextMenuOpenedOnTableHeaderEvent event) {

		}

		@Override
		public void onContextMenuOpenFromFooter(
				ContextMenuOpenedOnTableFooterEvent event) {

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

		contextMenu.setAsContextMenuOf(layout);
		contextMenu.setOpenAutomatically(true);

		layout.addComponent(new Label("Hello world labe!"));

		contextMenu.addContextMenuComponentListener(new ComponentListener() {

			@Override
			public void onContextMenuOpenFromComponent(
					ContextMenuOpenedOnComponentEvent event) {
				Notification.show("Open requested at " + event.getX() + " "
						+ event.getY() + " " + event.getComponent());
			}
		});

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
		tableContextMenu.addContextMenuTableListener(openListener);
		tableContextMenu.addItem("Table test item #1").setIcon(
				new ThemeResource("copy.png"));
		tableContextMenu.setAsTableContextMenu(table);

	}
}
