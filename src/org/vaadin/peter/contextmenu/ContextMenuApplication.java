package org.vaadin.peter.contextmenu;

import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItem;
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
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("contextmenu")
public class ContextMenuApplication extends UI {
	private static final long serialVersionUID = 4991155918522503460L;

	private final ContextMenuItemClickListener clickListener = new ContextMenuItemClickListener() {

		@Override
		public void contextMenuItemClicked(ContextMenuItemClickEvent event) {
			Notification.show(event.getSource().toString());
		}
	};

	private final ContextMenuOpenedListener.ComponentListener openComponentListener = new ContextMenuOpenedListener.ComponentListener() {

		@Override
		public void onContextMenuOpenFromComponent(
				ContextMenuOpenedOnComponentEvent event) {
			event.getContextMenu().removeAllItems();
			event.getContextMenu().addItem("Empty space");
			event.getContextMenu().open(event.getX(), event.getY());
		}
	};

	private final ContextMenuOpenedListener.TableListener openListener = new ContextMenuOpenedListener.TableListener() {

		@Override
		public void onContextMenuOpenFromRow(
				ContextMenuOpenedOnTableRowEvent event) {
			event.getContextMenu().removeAllItems();
			event.getContextMenu().addItem("Item " + event.getItemId());
		}

		@Override
		public void onContextMenuOpenFromHeader(
				ContextMenuOpenedOnTableHeaderEvent event) {
			event.getContextMenu().removeAllItems();
			event.getContextMenu().addItem("Item " + event.getPropertyId());
		}

		@Override
		public void onContextMenuOpenFromFooter(
				ContextMenuOpenedOnTableFooterEvent event) {
			event.getContextMenu().addItem("Item " + event.getPropertyId());
		}
	};

	private final ContextMenuOpenedListener.TreeListener treeItemListener = new ContextMenuOpenedListener.TreeListener() {

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

		final ContextMenu buttonContextMenu = new ContextMenu();
		final Button button = new Button("Test menu button");
		button.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				buttonContextMenu.open(button);
			}
		});

		ContextMenuItem buttonRootItem = buttonContextMenu.addItem("TestItem",
				new ThemeResource("img/basic/arrow.png"));
		buttonRootItem.addStyleName("RootFancyStyle");
		buttonRootItem.addItem("Sub TestItem", FontAwesome.GLASS).addStyleName(
				"SomeFancyStyle");
		buttonContextMenu.setAsContextMenuOf(button);

		layout.addComponent(button);
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
		tableContextMenu.addContextMenuComponentListener(openComponentListener);
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
		ContextMenuItem treeItem1 = treeContextMenu
				.addItem("Tree test item #1");
		treeItem1.setSeparatorVisible(true);
		treeItem1.addStyleName("treeStyle1");
		treeContextMenu.addItem("Tree test item #2").setEnabled(false);
		treeContextMenu.setAsTreeContextMenu(tree);

		layout.addComponent(tree);

		{
			// Example on how to change the caption and enabled state of an
			// existing context menu item
			final ContextMenu configurableContextMenu = new ContextMenu();
			configurableContextMenu.setOpenAutomatically(true);
			final ContextMenuItem configurableMenuItem = configurableContextMenu
					.addItem("Menu Item");
			final TextField captionField = new TextField();
			captionField.setWidth(120, Unit.PIXELS);
			captionField.setImmediate(true);
			captionField.setInputPrompt("Enter menu item caption here");
			captionField.addTextChangeListener(new TextChangeListener() {

				@Override
				public void textChange(TextChangeEvent event) {
					String newCaption = event.getText();
					configurableMenuItem.setCaption(newCaption);
				}
			});
			layout.addComponent(captionField);

			final CheckBox enabledCheckBox = new CheckBox("Menu item enabled");
			enabledCheckBox.setImmediate(true);
			enabledCheckBox.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					boolean newEnabled = (Boolean) event.getProperty()
							.getValue();
					configurableMenuItem.setEnabled(newEnabled);
				}
			});
			layout.addComponent(enabledCheckBox);

			Label menuLabel = new Label("Click here to open menu");
			configurableContextMenu.setAsContextMenuOf(menuLabel);
			layout.addComponent(menuLabel);

		}

	}
}
