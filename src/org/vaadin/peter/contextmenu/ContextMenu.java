package org.vaadin.peter.contextmenu;

import org.vaadin.peter.contextmenu.ContextMenuItem.ContextMenuItemClickListener;

import com.vaadin.terminal.Resource;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;

/**
 * ContextMenu is base interface for context menu implementations.
 * 
 * @author Peter / Vaadin Ltd
 */
public interface ContextMenu extends Component, HasComponents {

	/**
	 * Adds new item to context menu root with given caption.
	 * 
	 * @param caption
	 * @return reference to added item
	 */
	public ContextMenuItem addItem(String caption);

	/**
	 * Adds new item to context menu root with given icon without caption.
	 * 
	 * @param icon
	 * @return reference to added item
	 */
	public ContextMenuItem addItem(Resource icon);

	/**
	 * Adds new item to context menu root with given caption and icon.
	 * 
	 * @param caption
	 * @param icon
	 * @return reference to added item
	 */
	public ContextMenuItem addItem(String caption, Resource icon);

	/**
	 * Removes given item from context menu root.
	 * 
	 * @param contextMenuItem
	 */
	public void removeItem(ContextMenuItem contextMenuItem);

	/**
	 * Removes all items from the context menu root.
	 */
	public void removeAllItems();

	/**
	 * Opens context menu to given coordinates. Possible scroll position will be
	 * automatically added to given coordinates.
	 * 
	 * @param x
	 *            top left x of context menu root
	 * @param y
	 *            top left y of context menu root
	 */
	public void openAt(int x, int y);

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
	public void addListener(ContextMenuItemClickListener clickListener);

	/**
	 * Recursively removes given listener from all the items in the context
	 * menu. Items added to context menu after removing a listener will no
	 * longer receive the removed listener.
	 * 
	 * @param clickListener
	 */
	public void removeListener(ContextMenuItemClickListener clickListener);
}
