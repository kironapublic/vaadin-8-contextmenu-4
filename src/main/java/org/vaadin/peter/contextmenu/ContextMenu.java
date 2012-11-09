package org.vaadin.peter.contextmenu;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.vaadin.peter.contextmenu.client.ContextMenuClientRpc;
import org.vaadin.peter.contextmenu.client.ContextMenuServerRpc;
import org.vaadin.peter.contextmenu.client.ContextMenuState;
import org.vaadin.peter.contextmenu.client.ContextMenuState.ContextMenuItemState;

import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.Resource;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.Table.HeaderClickListener;
import com.vaadin.util.ReflectTools;

public class ContextMenu extends AbstractExtension {
	private static final long serialVersionUID = 4275181115413786498L;

	private Map<String, ContextMenuItem> items;

	private ContextMenuServerRpc serverRPC = new ContextMenuServerRpc() {
		private static final long serialVersionUID = 5622864428554337992L;

		@Override
		public void itemClicked(String itemId, boolean menuClosed) {
			ContextMenuItem item = items.get(itemId);

			item.notifyClickListeners();
			fireEvent(new ContextMenuItemClickEvent(item));
		}
	};

	public ContextMenu() {
		registerRpc(serverRPC);

		items = new HashMap<String, ContextMenu.ContextMenuItem>();
	}

	/**
	 * Adds new item to context menu root with given caption.
	 * 
	 * @param caption
	 * @return reference to added item
	 */
	public ContextMenuItem addItem(String caption) {
		ContextMenuItemState itemState = getState().addChild(caption,
				getNextId());

		ContextMenuItem item = new ContextMenuItem(itemState);
		items.put(itemState.id, item);

		return item;
	}

	protected String getNextId() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Adds new item to context menu root with given icon without caption.
	 * 
	 * @param icon
	 * @return reference to added item
	 */
	public ContextMenuItem addItem(Resource icon) {
		return null;
	}

	/**
	 * Adds new item to context menu root with given caption and icon.
	 * 
	 * @param caption
	 * @param icon
	 * @return reference to added item
	 */
	public ContextMenuItem addItem(String caption, Resource icon) {
		return addItem(caption);
	}

	/**
	 * Removes given item from context menu root.
	 * 
	 * @param contextMenuItem
	 */
	public void removeItem(ContextMenuItem contextMenuItem) {
		// items.remove(contextMenuItem);
	}

	/**
	 * Removes all items from the context menu root.
	 */
	public void removeAllItems() {
		getState().getRootItems().clear();
		items.clear();
	}

	/**
	 * Assigns this context menu as given table's row context menu
	 * 
	 * @param table
	 */
	public void applyForTableRows(final Table table) {
		extend(table);

		table.addItemClickListener(new ItemClickEvent.ItemClickListener() {

			@Override
			public void itemClick(ItemClickEvent event) {
				if (event.getButton() == MouseButton.RIGHT) {
					fireEvent(new ContextMenuOpenedOnTableRowEvent(
							ContextMenu.this, table, event.getItem(), event
									.getPropertyId()));
					getRpcProxy(ContextMenuClientRpc.class).showContextMenu(
							event.getClientX(), event.getClientY());
				}
			}
		});

		table.addHeaderClickListener(new HeaderClickListener() {

			@Override
			public void headerClick(HeaderClickEvent event) {
				if (event.getButton() == MouseButton.RIGHT) {
					fireEvent(new ContextMenuOpenedOnTableHeaderEvent(
							ContextMenu.this, table, event.getPropertyId()));
					getRpcProxy(ContextMenuClientRpc.class).showContextMenu(
							event.getClientX(), event.getClientY());
				}
			}
		});
	}

	public void applyFor(AbstractLayout layout) {
		super.extend(layout);
	}

	@Override
	protected ContextMenuState getState() {
		return (ContextMenuState) super.getState();
	}

	public class ContextMenuItem {
		private final ContextMenuItemState state;

		private List<ContextMenu.ContextMenuItemClickListener> clickListeners;

		protected ContextMenuItem(ContextMenuItemState itemState) {
			if (itemState == null) {
				throw new NullPointerException(
						"Context menu item state must not be null");
			}

			clickListeners = new ArrayList<ContextMenu.ContextMenuItemClickListener>();
			this.state = itemState;
		}

		public void notifyClickListeners() {
			for (ContextMenu.ContextMenuItemClickListener clickListener : clickListeners) {
				clickListener
						.contextMenuItemClicked(new ContextMenuItemClickEvent(
								this));
			}
		}

		public ContextMenuItem addItem(String caption) {
			ContextMenuItemState childItemState = state.addChild(caption,
					getNextId());
			ContextMenuItem item = new ContextMenuItem(childItemState);
			items.put(childItemState.id, item);
			markAsDirty();
			return item;
		}

		public void addItemClickListener(
				ContextMenu.ContextMenuItemClickListener clickListener) {
			this.clickListeners.add(clickListener);
		}

		@Override
		public boolean equals(Object other) {
			if (other == this) {
				return true;
			}

			if (other instanceof ContextMenuItem) {
				return state.id.equals(((ContextMenuItem) other).state.id);
			}

			return false;
		}

		@Override
		public int hashCode() {
			return state.id.hashCode();
		}
	}

	/**
	 * ContextMenuItemClickListener is listener for context menu items wanting
	 * to notify listeners about item click
	 */
	public interface ContextMenuItemClickListener extends EventListener,
			Serializable {

		public static final Method ITEM_CLICK_METHOD = ReflectTools.findMethod(
				ContextMenuItemClickListener.class, "contextMenuItemClicked",
				ContextMenuItemClickEvent.class);

		/**
		 * Called by the context menu item when it's clicked
		 * 
		 * @param event
		 *            containing the information of which item was clicked
		 */
		public void contextMenuItemClicked(ContextMenuItemClickEvent event);
	}

	/**
	 * ContextMenuItemClickEvent is an event produced by the context menu item
	 * when it is clicked. Event contains method for retrieving the clicked item
	 * and menu from which the click event originated.
	 */
	public static class ContextMenuItemClickEvent extends EventObject {
		private static final long serialVersionUID = -3301204853129409248L;

		public ContextMenuItemClickEvent(Object component) {
			super(component);
		}
	}

	public interface ContextMenuOpenedListener extends EventListener,
			Serializable {

		public static final Method MENU_OPENED_FROM_TABLE_ROW_METHOD = ReflectTools
				.findMethod(ContextMenuOpenedListener.class,
						"onContextMenuOpenFromRow",
						ContextMenuOpenedOnTableRowEvent.class);

		public static final Method MENU_OPENED_FROM_TABLE_HEADER_METHOD = ReflectTools
				.findMethod(ContextMenuOpenedListener.class,
						"onContextMenuOpenFromHeader",
						ContextMenuOpenedOnTableHeaderEvent.class);

		public void onContextMenuOpenFromRow(
				ContextMenuOpenedOnTableRowEvent event);

		public void onContextMenuOpenFromHeader(
				ContextMenuOpenedOnTableHeaderEvent event);
	}

	public static class ContextMenuOpenedOnTableHeaderEvent extends EventObject {
		private static final long serialVersionUID = -1220618848356241248L;

		private Object propertyId;

		private ContextMenu contextMenu;

		public ContextMenuOpenedOnTableHeaderEvent(ContextMenu contextMenu,
				Object component, Object propertyId) {
			super(component);

			this.contextMenu = contextMenu;
			this.propertyId = propertyId;
		}

		public ContextMenu getContextMenu() {
			return contextMenu;
		}

		public Object getPropertyId() {
			return propertyId;
		}
	}

	public static class ContextMenuOpenedOnTableRowEvent extends EventObject {
		private static final long serialVersionUID = -470218301318358912L;

		private ContextMenu contextMenu;
		private Object propertyId;
		private Item item;

		public ContextMenuOpenedOnTableRowEvent(ContextMenu contextMenu,
				Object component, Item item, Object propertyId) {
			super(component);

			this.contextMenu = contextMenu;
			this.item = item;
			this.propertyId = propertyId;
		}

		public ContextMenu getContextMenu() {
			return contextMenu;
		}

		public Item getItem() {
			return item;
		}

		public Object getPropertyId() {
			return propertyId;
		}
	}

	public void addItemClickListener(
			ContextMenu.ContextMenuItemClickListener clickListener) {
		addListener(ContextMenuItemClickEvent.class, clickListener,
				ContextMenuItemClickListener.ITEM_CLICK_METHOD);
	}

	public void addContextMenuOpenListener(
			ContextMenu.ContextMenuOpenedListener openListener) {
		addListener(ContextMenuOpenedOnTableRowEvent.class, openListener,
				ContextMenuOpenedListener.MENU_OPENED_FROM_TABLE_ROW_METHOD);
		addListener(ContextMenuOpenedOnTableHeaderEvent.class, openListener,
				ContextMenuOpenedListener.MENU_OPENED_FROM_TABLE_HEADER_METHOD);
	}

}
