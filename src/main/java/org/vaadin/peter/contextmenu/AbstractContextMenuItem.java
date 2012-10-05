package org.vaadin.peter.contextmenu;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.vaadin.peter.contextmenu.client.ui.ContextMenuItemServerRpc;

import com.vaadin.server.Resource;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Component;

/**
 * AbstractContextMenuItem is an abstract base class for context menu items that
 * can be displayed in the context menu
 * 
 * @author Peter / Vaadin Ltd
 */
public abstract class AbstractContextMenuItem extends
		AbstractComponentContainer implements ContextMenuItem {
	private static final long serialVersionUID = -8507589890813490495L;

	private AbstractContextMenu contextMenu;
	private final List<Component> items;

	private final ContextMenuListenerManager listenerManager;

	private final ContextMenuItemServerRpc rpc = new ContextMenuItemServerRpc() {
		private static final long serialVersionUID = -4860677919237817662L;

		@Override
		public void itemClicked(boolean menuClosed) {
			fireEvent(new ContextMenuItemClickEvent(
					AbstractContextMenuItem.this));

			if (contextMenu.getState().isShowing() != (!menuClosed)) {
				contextMenu.getState().setShowing(!menuClosed);
			}
		}
	};

	public AbstractContextMenuItem() {
		items = new LinkedList<Component>();
		listenerManager = new ContextMenuListenerManager();

		registerRpc(rpc);
	}

	/**
	 * Sets the root component which is the context menu root
	 * 
	 * @param contextMenu
	 */
	public void setRootMenuComponent(AbstractContextMenu contextMenu) {
		this.contextMenu = contextMenu;
	}

	public abstract Class<?> getContextMenuItemType();

	@Override
	public ContextMenuItem addItem(String caption) {
		AbstractContextMenuItem menuItem = buildContextMenuItem();
		menuItem.setCaption(caption);
		items.add(menuItem);

		super.addComponent(menuItem);

		listenerManager.addAllListenersForItem(menuItem);

		requestRepaint();
		return menuItem;
	}

	@Override
	public ContextMenuItem addItem(Resource icon) {
		AbstractContextMenuItem menuItem = buildContextMenuItem();
		menuItem.setIcon(icon);
		items.add(menuItem);

		super.addComponent(menuItem);

		listenerManager.addAllListenersForItem(menuItem);

		requestRepaint();
		return menuItem;
	}

	@Override
	public ContextMenuItem addItem(String caption, Resource icon) {
		AbstractContextMenuItem menuItem = buildContextMenuItem();
		menuItem.setCaption(caption);
		menuItem.setIcon(icon);
		items.add(menuItem);

		super.addComponent(menuItem);

		listenerManager.addAllListenersForItem(menuItem);

		requestRepaint();
		return menuItem;
	}

	@Override
	public boolean hasSubmenu() {
		return !items.isEmpty();
	}

	@Override
	public void removeItem(ContextMenuItem contextMenuItem) {
		items.remove(contextMenuItem);
		super.removeComponent(contextMenuItem);
		requestRepaint();
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

	@Override
	public void addListener(ContextMenuItemClickListener clickListener) {
		addListener(ContextMenuItemClickEvent.class, clickListener,
				ContextMenuItemClickListener.ITEM_CLICK_METHOD);
	}

	@Override
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
		listenerManager.addListener(clickListener);

		Iterator<Component> childIterator = iterator();

		addListener(clickListener);

		while (childIterator.hasNext()) {
			Component component = childIterator.next();

			if (component instanceof AbstractContextMenuItem) {
				AbstractContextMenuItem item = (AbstractContextMenuItem) component;
				item.addListenerRecursively(clickListener);
			}
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

		return listenerManager.hasListener(listener);
	}

	protected AbstractContextMenuItem buildContextMenuItem() {
		try {
			AbstractContextMenuItem menuItem = (AbstractContextMenuItem) getContextMenuItemType()
					.newInstance();

			menuItem.setRootMenuComponent(contextMenu);

			return menuItem;
		} catch (Exception e) {
			throw new RuntimeException(
					"Failed to instantiate proper context menu item");
		}
	}
}
