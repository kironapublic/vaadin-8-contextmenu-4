package org.vaadin.peter.contextmenu;

import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItemClickEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItemClickListener;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedListener;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedListener.ComponentListener;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedOnComponentEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedOnTableFooterEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedOnTableHeaderEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedOnTableRowEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuOpenedOnTreeItemEvent;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Item;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("test")
public class ContextMenuApplication extends UI {
	private static final long serialVersionUID = 4991155918522503460L;

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
			Notification.show("Table item clicked " + event.getItemId() + " "
					+ event.getPropertyId());
		}

		@Override
		public void onContextMenuOpenFromHeader(
				ContextMenuOpenedOnTableHeaderEvent event) {
			Notification.show("Table header clicked " + event.getPropertyId());
		}

		@Override
		public void onContextMenuOpenFromFooter(
				ContextMenuOpenedOnTableFooterEvent event) {
			Notification.show("Table footer clicked " + event.getPropertyId());
		}
	};

	private ContextMenuOpenedListener.TreeListener treeItemListener = new ContextMenuOpenedListener.TreeListener() {

		@Override
		public void onContextMenuOpenFromTreeItem(
				ContextMenuOpenedOnTreeItemEvent event) {
			Notification.show("Tree item clicked " + event.getItemId());
		}
	};

	@Override
	protected void init(VaadinRequest request) {

		VerticalLayout layout = new VerticalLayout();

		setContent(layout);

		final ContextMenu contextMenu = new ContextMenu();

		contextMenu.addItem("Test item #1").addItem("Child #1")
				.addItem("Child 2").addItemClickListener(clickListener);
		contextMenu.addItem("Test item #2");

		contextMenu.setAsContextMenuOf(layout);
		contextMenu.setOpenAutomatically(false);

		layout.addComponent(new Label("Hello world labe!"));

		contextMenu.addContextMenuComponentListener(new ComponentListener() {

			@Override
			public void onContextMenuOpenFromComponent(
					ContextMenuOpenedOnComponentEvent event) {
				Notification.show("Open requested at " + event.getX() + " "
						+ event.getY() + " " + event.getSource());

				// If set open automatically was true, this listener wouldn't be
				// called and context menu would be opened on client side
				// without server round trip. When set automatically is false
				// developer may affect contents of the menu before opening it.
				contextMenu.open(event.getX(), event.getY());
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

		Tree tree = new Tree();

		tree.addItem("1");
		tree.addItem("2");
		tree.addItem("3");

		tree.setParent("3", "2");
		tree.setParent("2", "1");
		tree.setChildrenAllowed("1", true);
		tree.setChildrenAllowed("2", true);
		tree.setChildrenAllowed("3", false);

		ContextMenu treeContextMenu = new ContextMenu();
		treeContextMenu.addContextMenuTreeListener(treeItemListener);
		treeContextMenu.addItem("Tree test item #1");
		treeContextMenu.addItem("Tree test item #2");
		treeContextMenu.setAsTreeContextMenu(tree);

		layout.addComponent(tree);

	}
}
