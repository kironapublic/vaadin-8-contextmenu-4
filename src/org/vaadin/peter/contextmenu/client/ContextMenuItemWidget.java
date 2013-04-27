package org.vaadin.peter.contextmenu.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;

/**
 * ContextMenuItemWidget is client side widget that represents one menu item in
 * context menu.
 * 
 * @author Peter Lehto / Vaadin Ltd
 */
public class ContextMenuItemWidget extends FocusWidget {
	private final FlowPanel root;

	protected ImageElement icon;
	private final FlowPanel iconContainer;
	private final Label text;

	private ContextMenuOverlay subMenu;

	private ContextMenuItemWidget parentItem;
	private ContextMenuOverlay overlay;

	private ContextMenuWidget rootComponent;

	private String id;

	public ContextMenuItemWidget() {
		root = new FlowPanel();
		root.setStylePrimaryName("v-context-menu-item-basic");

		setElement(root.getElement());

		root.addStyleName("v-context-submenu");

		iconContainer = new FlowPanel();
		iconContainer.setStyleName("v-context-menu-item-basic-icon-container");

		text = new Label();
		text.setStyleName("v-context-menu-item-basic-text");

		root.add(iconContainer);
		root.add(text);
	}

	@Override
	public void setFocus(boolean focused) {
		if (hasSubMenu()) {
			subMenu.setFocus(false);
		}

		super.setFocus(focused);

		if (!focused) {
			DOM.releaseCapture(getElement());
		}
	}

	/**
	 * @return true if this item has a sub menu
	 */
	public boolean hasSubMenu() {
		return subMenu != null && subMenu.getNumberOfItems() > 0;
	}

	/**
	 * Hides the sub menu that's been opened from this item
	 */
	public void hideSubMenu() {
		if (hasSubMenu()) {
			subMenu.hide();
			removeStyleName("v-context-menu-item-basic-open");
		}
	}

	/**
	 * @return true if this item is an item in the root menu, false otherwise
	 */
	public boolean isRootItem() {
		return parentItem == null;
	}

	/**
	 * Sets the menu component to which this item belongs to
	 * 
	 * @param owner
	 */
	public void setOverlay(ContextMenuOverlay owner) {
		this.overlay = owner;
	}

	/**
	 * Sets parent item meaning that this item is in the sub menu of given
	 * parent item.
	 * 
	 * @param parentItem
	 */
	public void setParentItem(ContextMenuItemWidget parentItem) {
		this.parentItem = parentItem;
	}

	/**
	 * @return menu item that opened the menu to which this item belongs
	 */
	public ContextMenuItemWidget getParentItem() {
		return parentItem;
	}

	/**
	 * @return true if this menu has a sub menu and it's open
	 */
	public boolean isSubmenuOpen() {
		return hasSubMenu() && subMenu.isShowing();
	}

	/**
	 * Removes all the items from the submenu of this item. If this menu item
	 * does not have a sub menu, this call has no effect.
	 */
	public void clearItems() {
		if (hasSubMenu()) {
			subMenu.clearItems();
		}
	}

	/**
	 * Adds given context menu item into the sub menu of this item.
	 * 
	 * @param contextMenuItem
	 */
	public void addSubMenuItem(ContextMenuItemWidget contextMenuItem) {
		if (!hasSubMenu()) {
			subMenu = new ContextMenuOverlay();
			setStylePrimaryName("v-context-menu-item-basic-submenu");
		}

		contextMenuItem.setParentItem(this);
		subMenu.addMenuItem(contextMenuItem);
	}

	public void setCaption(String caption) {
		text.setText(caption);
	}

	public void setIcon(String url) {
		if (url == null) {
			iconContainer.clear();
			icon = null;
		} else {
			icon = Document.get().createImageElement();
			icon.setClassName("v-icon");
			icon.setSrc(url);
			iconContainer.getElement().appendChild(icon);
		}
	}

	public void setRootComponent(ContextMenuWidget rootComponent) {
		this.rootComponent = rootComponent;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void closeSiblingMenus() {
		overlay.closeSubMenus();
	}

	protected void selectLowerSibling() {
		setFocus(false);
		overlay.selectItemAfter(ContextMenuItemWidget.this);

	}

	protected void selectUpperSibling() {
		setFocus(false);
		overlay.selectItemBefore(ContextMenuItemWidget.this);
	}

	protected void closeThisAndSelectParent() {
		if (!isRootItem()) {
			setFocus(false);
			parentItem.hideSubMenu();
			parentItem.setFocus(true);
		}
	}

	/**
	 * Called when context menu item is clicked or is focused and enter is
	 * pressed.
	 * 
	 * @return true if context menu was closed after the click, false otherwise
	 */
	protected boolean onItemClicked() {
		if (isEnabled()) {
			overlay.closeSubMenus();

			if (hasSubMenu()) {
				openSubMenu();
				return false;
			} else {
				if (isRootItem()) {
					closeContextMenu();
				} else {
					parentItem.closeContextMenu();
				}

				return true;
			}
		}

		return false;
	}

	private void closeContextMenu() {
		if (isRootItem()) {
			rootComponent.hide();
		} else {
			parentItem.closeContextMenu();
		}
	}

	/**
	 * Programmatically opens the sub menu of this item.
	 */
	private void openSubMenu() {
		if (isEnabled() && hasSubMenu() && !subMenu.isShowing()) {
			overlay.closeSubMenus();

			setFocus(false);
			addStyleName("v-context-menu-item-basic-open");
			subMenu.openNextTo(this);
		}
	}

	/**
	 * @param nativeEvent
	 * @return true if given event targets the overlay of this menu item or
	 *         overlay of any of this item's child item.
	 */
	public boolean eventTargetsPopup(Event nativeEvent) {
		if (overlay.eventTargetsPopup(nativeEvent)) {
			return true;
		}

		if (hasSubMenu()) {
			for (ContextMenuItemWidget item : subMenu.getMenuItems()) {
				if (item.eventTargetsPopup(nativeEvent)) {
					return true;
				}
			}
		}

		return false;
	}

	public void setSeparatorVisible(boolean separatorVisible) {
		if (separatorVisible) {
			root.addStyleName("v-context-menu-item-separator");
		} else {
			root.removeStyleName("v-context-menu-item-separator");
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (enabled) {
			root.removeStyleName("v-context-menu-item-disabled");
		} else {
			root.addStyleName("v-context-menu-item-disabled");
		}
	}
}
