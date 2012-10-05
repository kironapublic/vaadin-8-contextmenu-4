package org.vaadin.peter.contextmenu;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.vaadin.peter.contextmenu.ContextMenuItem.ContextMenuItemClickListener;
import org.vaadin.peter.contextmenu.client.ui.ContextMenuState;


import com.vaadin.server.Resource;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Component;

public abstract class AbstractContextMenu extends AbstractComponentContainer
		implements ContextMenu {
	private static final long serialVersionUID = 4275181115413786498L;

	private final List<Component> items;

	private final ContextMenuListenerManager listenerManager;

	public AbstractContextMenu() {
		items = new LinkedList<Component>();
		getState().setShowing(false);

		listenerManager = new ContextMenuListenerManager();
	}

	@Override
	public ContextMenuItem addItem(String caption) {
		AbstractContextMenuItem item = buildContextMenuItem();
		item.setCaption(caption);
		items.add(item);
		super.addComponent(item);

		listenerManager.addAllListenersForItem(item);

		requestRepaint();
		return item;
	}

	@Override
	public ContextMenuItem addItem(Resource icon) {
		AbstractContextMenuItem item = buildContextMenuItem();
		item.setIcon(icon);
		items.add(item);
		super.addComponent(item);

		listenerManager.addAllListenersForItem(item);

		requestRepaint();
		return item;
	}

	@Override
	public ContextMenuItem addItem(String caption, Resource icon) {
		AbstractContextMenuItem item = buildContextMenuItem();
		item.setCaption(caption);
		item.setIcon(icon);
		items.add(item);
		super.addComponent(item);

		listenerManager.addAllListenersForItem(item);

		requestRepaint();
		return item;
	}

	@Override
	public void removeItem(ContextMenuItem contextMenuItem) {
		items.remove(contextMenuItem);
		super.removeComponent(contextMenuItem);
		requestRepaint();
	}

	@Override
	public void removeAllItems() {
		items.clear();
		super.removeAllComponents();
		requestRepaint();
	}

	@Override
	public void openAt(int x, int y) {
		if (!isEnabled()) {
			return;
		}

		setVisible(true);

		getState().setRootMenuX(x);
		getState().setRootMenuY(y);

		getState().setShowing(true);

		requestRepaint();
	}

	@Override
	public ContextMenuState getState() {
		return (ContextMenuState) super.getState();
	}

	public abstract Class<?> getContextMenuItemType();

	private AbstractContextMenuItem buildContextMenuItem() {
		try {
			AbstractContextMenuItem menuItem = (AbstractContextMenuItem) getContextMenuItemType()
					.newInstance();

			menuItem.setRootMenuComponent(this);

			return menuItem;
		} catch (Exception e) {
			throw new RuntimeException(
					"Failed to instantiate proper context menu item");
		}
	}

	@Override
	public Iterator<Component> getComponentIterator() {
		return items.iterator();
	}

	@Override
	public void replaceComponent(Component oldComponent, Component newComponent) {
		throw new UnsupportedOperationException(
				"Cannot replace context menu items");
	}

	@Override
	public int getComponentCount() {
		return items.size();
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
	public void addListener(ContextMenuItemClickListener listener) {
		listenerManager.addListener(listener);

		Iterator<Component> childIterator = iterator();

		while (childIterator.hasNext()) {
			Component component = childIterator.next();

			if (component instanceof AbstractContextMenuItem) {
				AbstractContextMenuItem item = (AbstractContextMenuItem) component;
				item.addListenerRecursively(listener);
			}
		}
	}

	@Override
	public void removeListener(ContextMenuItemClickListener listener) {

	}
}
