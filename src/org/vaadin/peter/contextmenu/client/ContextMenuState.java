package org.vaadin.peter.contextmenu.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.shared.AbstractComponentState;

public class ContextMenuState extends AbstractComponentState {
	private static final long serialVersionUID = -247856391284942254L;

	private List<ContextMenuItemState> rootItems;

	private boolean openAutomatically;

	public ContextMenuState() {
		rootItems = new ArrayList<ContextMenuState.ContextMenuItemState>();
	}

	public ContextMenuItemState addChild(String caption, String id) {
		ContextMenuItemState rootItem = new ContextMenuItemState();
		rootItem.caption = caption;
		rootItem.id = id;

		rootItems.add(rootItem);

		return rootItem;
	}

	public List<ContextMenuItemState> getRootItems() {
		return rootItems;
	}

	public void setRootItems(List<ContextMenuItemState> rootItems) {
		this.rootItems = rootItems;
	}

	/**
	 * @return true if open automatically is on. If open automatically is on, it
	 *         means that context menu will always be opened when it's host
	 *         component is right clicked. If automatic opening is turned off,
	 *         context menu will only open when server side open(x, y) is
	 *         called.
	 */
	public boolean isOpenAutomatically() {
		return openAutomatically;
	}

	/**
	 * Enables or disables open automatically feature. If open automatically is
	 * on, it means that context menu will always be opened when it's host
	 * component is right clicked. If automatic opening is turned off, context
	 * menu will only open when server side open(x, y) is called.
	 * 
	 * @param openAutomatically
	 */
	public void setOpenAutomatically(boolean openAutomatically) {
		this.openAutomatically = openAutomatically;
	}

	public static class ContextMenuItemState implements Serializable {
		private static final long serialVersionUID = 3836772122928080543L;

		private List<ContextMenuItemState> children;

		public String caption;

		public String id;
		
		public boolean separator;

		public boolean enabled = true;

		public ContextMenuItemState() {
			children = new ArrayList<ContextMenuState.ContextMenuItemState>();
		}

		public ContextMenuItemState addChild(String caption, String id) {
			ContextMenuItemState child = new ContextMenuItemState();
			child.caption = caption;
			child.id = id;

			children.add(child);

			return child;
		}

		public List<ContextMenuItemState> getChildren() {
			return children;
		}

		public void setChildren(List<ContextMenuItemState> children) {
			this.children = children;
		}
	}
}
