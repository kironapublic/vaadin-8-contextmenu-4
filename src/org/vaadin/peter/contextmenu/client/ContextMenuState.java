package org.vaadin.peter.contextmenu.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vaadin.shared.AbstractComponentState;

public class ContextMenuState extends AbstractComponentState {
	private static final long serialVersionUID = -247856391284942254L;

	private List<ContextMenuItemState> rootItems;

	private boolean openAutomatically;
	private boolean hideAutomatically;

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

	/**
	 * @return true if context menu is hidden automatically
	 */
	public boolean isHideAutomatically() {
		return hideAutomatically;
	}

	/**
	 * Enables or disables automatic hiding of context menu
	 * 
	 * @param hideAutomatically
	 */
	public void setHideAutomatically(boolean hideAutomatically) {
		this.hideAutomatically = hideAutomatically;
	}

	public static class ContextMenuItemState implements Serializable {
		private static final long serialVersionUID = 3836772122928080543L;

		private List<ContextMenuItemState> children;

		public String caption;

		public String id;

		public boolean separator;

		public boolean enabled = true;

		private Set<String> styles;

		public ContextMenuItemState() {
			children = new ArrayList<ContextMenuState.ContextMenuItemState>();
			styles = new HashSet<String>();
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

		public void removeChild(ContextMenuItemState child) {
			children.remove(child);
		}

		public Set<String> getStyles() {
			return styles;
		}

		public void setStyles(Set<String> styleNames) {
			this.styles = styleNames;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}

			if (obj instanceof ContextMenuItemState) {
				return this.id.equals(((ContextMenuItemState) obj).id);
			}

			return false;
		}

		@Override
		public int hashCode() {
			return id.hashCode();
		}
	}
}
