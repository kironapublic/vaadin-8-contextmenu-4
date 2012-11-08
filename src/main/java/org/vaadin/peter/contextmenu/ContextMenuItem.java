package org.vaadin.peter.contextmenu;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.vaadin.peter.contextmenu.client.ContextMenuItemServerRpc;

import com.vaadin.server.Resource;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

/**
 * AbstractContextMenuItem is an abstract base class for context menu items that
 * can be displayed in the context menu
 * 
 * @author Peter / Vaadin Ltd
 */
public class ContextMenuItem extends AbstractComponentContainer {
	private static final long serialVersionUID = -8507589890813490495L;

	private ContextMenu contextMenu;
	private final List<Component> items;

	private final ContextMenuItemServerRpc rpc = new ContextMenuItemServerRpc() {
		private static final long serialVersionUID = -4860677919237817662L;

		@Override
		public void itemClicked(boolean menuClosed) {
			fireEvent(new ContextMenuItemClickEvent(ContextMenuItem.this));

			if (contextMenu.getState().isShowing() != (!menuClosed)) {
				contextMenu.getState().setShowing(!menuClosed);
			}
		}
	};

	public ContextMenuItem() {
		items = new LinkedList<Component>();

		registerRpc(rpc);
	}

	/**
	 * Sets the root component which is the context menu root
	 * 
	 * @param contextMenu
	 */
	public void setRootMenuComponent(ContextMenu contextMenu) {
		this.contextMenu = contextMenu;
	}

	public ContextMenuItem addItem(String caption) {
		ContextMenuItem menuItem = buildContextMenuItem();
		menuItem.setCaption(caption);
		items.add(menuItem);

		super.addComponent(menuItem);

		return menuItem;
	}

	public ContextMenuItem addItem(Resource icon) {
		ContextMenuItem menuItem = buildContextMenuItem();
		menuItem.setIcon(icon);
		items.add(menuItem);

		super.addComponent(menuItem);

		return menuItem;
	}

	public ContextMenuItem addItem(String caption, Resource icon) {
		ContextMenuItem menuItem = buildContextMenuItem();
		menuItem.setCaption(caption);
		menuItem.setIcon(icon);
		items.add(menuItem);

		super.addComponent(menuItem);

		return menuItem;
	}

	public boolean hasSubmenu() {
		return !items.isEmpty();
	}

	public void removeItem(ContextMenuItem contextMenuItem) {
		items.remove(contextMenuItem);
		super.removeComponent(contextMenuItem);
	}

	@Override
	public void addComponent(Component c) {
		throw new UnsupportedOperationException(
				"Cannot add components to context menu, use addItem methods instead");
	}

	@Override
	public void removeComponent(Component c) {
		throw new UnsupportedOperationException(
				"Cannot remove components from context menu, use removeItem method instead");
	}

	@Override
	public void removeAllComponents() {
		throw new UnsupportedOperationException(
				"Cannot remove all components from context menu, use removeAllItems method instead");
	}

	public void addClickListener(ContextMenuItemClickListener clickListener) {
		addListener(ContextMenuItemClickEvent.class, clickListener,
				ContextMenuItemClickListener.ITEM_CLICK_METHOD);
	}

	public void removeListener(ContextMenuItemClickListener clickListener) {
		removeListener(ContextMenuItemClickEvent.class, clickListener,
				ContextMenuItemClickListener.ITEM_CLICK_METHOD);
	}

	@Override
	public Iterator<Component> getComponentIterator() {
		return items.iterator();
	}

	@Override
	public void replaceComponent(Component oldComponent, Component newComponent) {
		throw new UnsupportedOperationException(
				"Cannot replace items in sub menus");
	}

	@Override
	public int getComponentCount() {
		return items.size();
	}

	/**
	 * Recursively adds given listener to this item and to every child items
	 * 
	 * @param clickListener
	 */
	public void addListenerRecursively(
			ContextMenuItemClickListener clickListener) {

		Iterator<Component> childIterator = iterator();

		addClickListener(clickListener);

		while (childIterator.hasNext()) {
			Component component = childIterator.next();

			ContextMenuItem item = (ContextMenuItem) component;
			item.addListenerRecursively(clickListener);
		}
	}

	/**
	 * @param listener
	 * @return true if this menu item has given listener
	 */
	public boolean hasListener(ContextMenuItemClickListener listener) {
		if (getListeners(ContextMenuItemClickEvent.class).contains(listener)) {
			return true;
		}

		return false;
	}

	protected ContextMenuItem buildContextMenuItem() {
		try {
			ContextMenuItem menuItem = new ContextMenuItem();

			return menuItem;
		} catch (Exception e) {
			throw new RuntimeException(
					"Failed to instantiate proper context menu item");
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
	public class ContextMenuItemClickEvent extends Component.Event {
		private static final long serialVersionUID = -3301204853129409248L;

		public ContextMenuItemClickEvent(ContextMenuItem clickedItem) {
			super(clickedItem);
		}

		/**
		 * @return ContextMenuItem that was clicked
		 */
		public ContextMenuItem getClickedItem() {
			return (ContextMenuItem) getSource();
		}
	}
}
