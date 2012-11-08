package org.vaadin.peter.contextmenu;

import org.vaadin.peter.contextmenu.client.ContextMenuState;
import org.vaadin.peter.contextmenu.client.ContextMenuState.ContextMenuItemState;

import com.vaadin.server.AbstractExtension;
import com.vaadin.server.Resource;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;

public class ContextMenu extends AbstractExtension {
	private static final long serialVersionUID = 4275181115413786498L;

	public ContextMenu() {
		getState().showing = false;
	}

	/**
	 * Adds new item to context menu root with given caption.
	 * 
	 * @param caption
	 * @return reference to added item
	 */
	public ContextMenuItem addItem(String caption) {
		ContextMenuItemState itemState = getState().addChild(caption);

		return new ContextMenuItem(itemState);
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

		protected ContextMenuItem(ContextMenuItemState itemState) {
			this.state = itemState;
		}

		public ContextMenuItem addItem(String caption) {
			ContextMenuItem item = new ContextMenuItem(state.addChild(caption));
			markAsDirty();
			return item;
		}
	}

}
