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

import org.vaadin.peter.contextmenu.client.ContextMenuServerRpc;
import org.vaadin.peter.contextmenu.client.ContextMenuState;
import org.vaadin.peter.contextmenu.client.ContextMenuState.ContextMenuItemState;

import com.vaadin.server.AbstractExtension;
import com.vaadin.server.Resource;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;
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
		getState().showing = false;

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
		// items.clear();
	}

	public void extend(Table table) {
		super.extend(table);
	}

	public void extend(Tree tree) {
		super.extend(tree);
	}

	public void extend(AbstractLayout layout) {
		super.extend(layout);
	}

	@Override
	public ContextMenuState getState() {
		return (ContextMenuState) super.getState();
	}

	public class ContextMenuItem {
		private final ContextMenuItemState state;

		private List<ContextMenu.ItemClickListener> clickListeners;

		protected ContextMenuItem(ContextMenuItemState itemState) {
			if (itemState == null) {
				throw new NullPointerException(
						"Context menu item state must not be null");
			}

			clickListeners = new ArrayList<ContextMenu.ItemClickListener>();
			this.state = itemState;
		}

		public void notifyClickListeners() {
			for (ContextMenu.ItemClickListener clickListener : clickListeners) {
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
				ContextMenu.ItemClickListener clickListener) {
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
	public interface ItemClickListener extends EventListener, Serializable {

		public static final Method ITEM_CLICK_METHOD = ReflectTools.findMethod(
				ItemClickListener.class, "contextMenuItemClicked",
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

	public void addItemClickListener(ContextMenu.ItemClickListener clickListener) {
		addListener(ContextMenuItemClickEvent.class, clickListener,
				ItemClickListener.ITEM_CLICK_METHOD);
	}

}
