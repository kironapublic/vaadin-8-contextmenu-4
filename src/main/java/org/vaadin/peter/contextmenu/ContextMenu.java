package org.vaadin.peter.contextmenu;

import java.util.LinkedList;
import java.util.List;

import org.vaadin.peter.contextmenu.ContextMenuItem.ContextMenuItemClickListener;
import org.vaadin.peter.contextmenu.client.ContextMenuState;

import com.vaadin.server.AbstractExtension;
import com.vaadin.server.Resource;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;

public class ContextMenu extends AbstractExtension {
	private static final long serialVersionUID = 4275181115413786498L;

	private final List<Component> items;

	public ContextMenu() {
		items = new LinkedList<Component>();
		getState().setShowing(false);
	}

	/**
	 * Adds new item to context menu root with given caption.
	 * 
	 * @param caption
	 * @return reference to added item
	 */
	public ContextMenuItem addItem(String caption) {
		ContextMenuItem item = buildContextMenuItem();
		item.setCaption(caption);
		items.add(item);

		return item;
	}

	/**
	 * Adds new item to context menu root with given icon without caption.
	 * 
	 * @param icon
	 * @return reference to added item
	 */
	public ContextMenuItem addItem(Resource icon) {
		ContextMenuItem item = buildContextMenuItem();
		item.setIcon(icon);
		items.add(item);

		return item;
	}

	/**
	 * Adds new item to context menu root with given caption and icon.
	 * 
	 * @param caption
	 * @param icon
	 * @return reference to added item
	 */
	public ContextMenuItem addItem(String caption, Resource icon) {
		ContextMenuItem item = buildContextMenuItem();
		item.setCaption(caption);
		item.setIcon(icon);
		items.add(item);

		return item;
	}

	/**
	 * Removes given item from context menu root.
	 * 
	 * @param contextMenuItem
	 */
	public void removeItem(ContextMenuItem contextMenuItem) {
		items.remove(contextMenuItem);
	}

	/**
	 * Removes all items from the context menu root.
	 */
	public void removeAllItems() {
		items.clear();
	}

	/**
	 * Opens context menu to given coordinates. Possible scroll position will be
	 * automatically added to given coordinates.
	 * 
	 * @param x
	 *            top left x of context menu root
	 * @param y
	 *            top left y of context menu root
	 */
	public void openAt(int x, int y) {
		getState().setRootMenuX(x);
		getState().setRootMenuY(y);

		getState().setShowing(true);
	}

	public void extend(Table table) {
		super.extend(table);
	}

	public void extend(Tree tree) {
		super.extend(tree);
	}

	public void extend(AbstractComponentContainer layout) {
		super.extend(layout);
	}

	@Override
	public ContextMenuState getState() {
		return (ContextMenuState) super.getState();
	}

	private ContextMenuItem buildContextMenuItem() {
		try {
			ContextMenuItem menuItem = new ContextMenuItem();

			return menuItem;
		} catch (Exception e) {
			throw new RuntimeException(
					"Failed to instantiate proper context menu item");
		}
	}

	/**
	 * Recursively adds given listener to all the item in the context menu. If
	 * more items are added after adding the listener, new items added
	 * afterwards will also get the same listener. This means that there is no
	 * difference between the order of adding listener or items. All menu items
	 * will have all the listeners added by calling this method. If you want to
	 * add item specific listeners you can all
	 * <code>ContextMenuItem.addListener</code> method.
	 * 
	 * @param clickListener
	 */
	public void addListener(ContextMenuItemClickListener listener) {

	}

	/**
	 * Recursively removes given listener from all the items in the context
	 * menu. Items added to context menu after removing a listener will no
	 * longer receive the removed listener.
	 */
	public void removeListener(ContextMenuItemClickListener listener) {

	}
}
