package org.vaadin.peter.contextmenu;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.EventListener;

import com.vaadin.server.Resource;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.util.ReflectTools;

/**
 * ContextMenuItem is base interface for all the items within the context menu.
 */
public interface ContextMenuItem extends Component, HasComponents {

	/**
	 * Adds new context menu item as this item's sub menu
	 * 
	 * @param caption
	 *            item caption as string
	 * @return ContextMenuItem describing the sub menu item
	 */
	public ContextMenuItem addItem(String caption);

	/**
	 * Adds new context menu item as this item's sub menu
	 * 
	 * @param icon
	 *            item icon as Resource
	 * @return ContextMenuItem describing the sub menu item
	 */
	public ContextMenuItem addItem(Resource icon);

	/**
	 * Adds new context menu item as this item's sub menu
	 * 
	 * @param caption
	 *            item caption as string
	 * @param icon
	 *            item icon as Resource
	 * @return ContextMenuItem describing the sub menu item
	 */
	public ContextMenuItem addItem(String caption, Resource icon);

	/**
	 * @return true if this item has a sub menu
	 */
	public boolean hasSubmenu();

	/**
	 * Removes given context menu item from this item's sub items
	 * 
	 * @param contextMenuItem
	 */
	public void removeItem(ContextMenuItem contextMenuItem);

	/**
	 * Set or updates the item description tooltip
	 * 
	 * @param description
	 */
	public void setDescription(String description);

	/**
	 * Adds click listener specifically to this menu item. Listener will be
	 * called only when this menu item is clicked or if this menu item has a sub
	 * menu, when it's opened.
	 * 
	 * @param clickListener
	 */
	public void addListener(ContextMenuItemClickListener clickListener);

	/**
	 * Removes click listener specifically from this item. Listener will no
	 * longer be notified about clicks or opening sub menus of this item.
	 * 
	 * @param clickListener
	 */
	public void removeListener(ContextMenuItemClickListener clickListener);

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
