package org.vaadin.peter.contextmenu.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.vaadin.client.ui.Icon;

public class ContextMenuItemWidget extends FocusWidget {
	private final FlowPanel root;

	protected Icon icon;
	private final FlowPanel iconContainer;
	private final Label text;

	private ContextMenuOverlay subMenu;

	private ContextMenuItemWidget parentItem;
	private ContextMenuOverlay owner;

	private ContextMenuWidget rootComponent;

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
	public void setOwner(ContextMenuOverlay owner) {
		this.owner = owner;
	}

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

	public void clearItems() {
		if (hasSubMenu()) {
			subMenu.clearItems();
		}
	}

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

	public void setIcon(Icon icon) {
		this.icon = icon;
		iconContainer.getElement().appendChild(icon.getElement());
	}

	public void removeIcon() {
		iconContainer.clear();
		icon = null;
	}

	public void setRootComponent(ContextMenuWidget rootComponent) {
		this.rootComponent = rootComponent;
	}

	public void closeSiblingMenus() {
		owner.closeSubMenus();
	}

	protected void selectLowerSibling() {
		setFocus(false);
		owner.selectItemAfter(ContextMenuItemWidget.this);

	}

	protected void selectUpperSibling() {
		setFocus(false);
		owner.selectItemBefore(ContextMenuItemWidget.this);
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
			owner.closeSubMenus();

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

	private void openSubMenu() {
		if (isEnabled() && hasSubMenu() && !subMenu.isShowing()) {
			owner.closeSubMenus();

			setFocus(false);
			addStyleName("v-context-menu-item-basic-open");
			subMenu.openNextTo(this);
			subMenu.setFocus(true);
		}
	}

	public boolean eventTargetsPopup(Event nativeEvent) {
		if (owner.eventTargetsPopup(nativeEvent)) {
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
}
