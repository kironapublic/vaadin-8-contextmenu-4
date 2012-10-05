package org.vaadin.peter.contextmenu.client.ui;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.vaadin.client.ui.VOverlay;

/**
 * Menu is a visible item of ContextMenu component. For every child menu new
 * instance of Menu class will be instantiated.
 * 
 * @author Peter Lehto / IT Mill Oy Ltd
 */
class VBasicMenu extends VOverlay {
	private final FlowPanel root;

	private final List<VContextMenuBasicItem> menuItems;

	private final CloseHandler<PopupPanel> closeHandler = new CloseHandler<PopupPanel>() {

		@Override
		public void onClose(CloseEvent<PopupPanel> event) {
			unfocusAll();
		}
	};

	public VBasicMenu() {
		super(false, false, true);

		addCloseHandler(closeHandler);
		setStyleName("v-context-menu-container");

		root = new FlowPanel();
		root.setStyleName("v-context-menu");

		menuItems = new LinkedList<VContextMenuBasicItem>();

		add(root);
	}

	public boolean isSubmenuOpen() {
		for (VContextMenuBasicItem item : menuItems) {
			if (item.isSubmenuOpen()) {
				return true;
			}
		}

		return false;
	}

	private void focusFirstItem() {
		if (menuItems.size() > 0) {
			menuItems.iterator().next().setFocus(true);
		}
	}

	public void setFocus(boolean focused) {
		unfocusAll();

		if (focused) {
			focusFirstItem();
		}
	}

	private void unfocusAll() {
		for (VContextMenuBasicItem item : menuItems) {
			item.setFocus(false);
		}
	}

	protected void normalizeItemWidths() {
		int widestItemWidth = getWidthOfWidestItem();

		for (VContextMenuBasicItem item : menuItems) {
			if (item.getOffsetWidth() <= widestItemWidth) {
				item.setWidth(widestItemWidth + "px");
			}
		}
	}

	protected boolean eventTargetsPopup(NativeEvent event) {
		EventTarget target = event.getEventTarget();
		if (Element.is(target)) {
			return getElement().isOrHasChild(Element.as(target));
		}
		return false;
	}

	private int getWidthOfWidestItem() {
		int maxWidth = 0;

		for (VContextMenuBasicItem item : menuItems) {
			int itemWidth = item.getOffsetWidth() + 1;

			if (itemWidth > maxWidth) {
				maxWidth = itemWidth;
			}
		}

		return maxWidth;
	}

	@Override
	public void hide() {
		unfocusAll();
		closeSubMenus();
		super.hide();
	}

	public List<VContextMenuBasicItem> getMenuItems() {
		return menuItems;
	}

	/**
	 * @return number of visible items in this menu
	 */
	public int getNumberOfItems() {
		return this.menuItems.size();
	}

	public void openNextTo(VContextMenuBasicItem parentMenuItem) {
		int left = parentMenuItem.getAbsoluteLeft()
				+ parentMenuItem.getOffsetWidth();
		int top = parentMenuItem.getAbsoluteTop();

		setPopupPosition(left, top);

		show();
		normalizeItemWidths();
	}

	public void closeSubMenus() {
		for (VContextMenuBasicItem child : menuItems) {
			child.hideSubMenu();
		}
	}

	public void selectItemBefore(VContextMenuBasicItem item) {
		int index = menuItems.indexOf(item);

		index -= 1;

		if (index < 0) {
			index = menuItems.size() - 1;
		}

		VContextMenuBasicItem itemToSelect = menuItems.get(index);
		itemToSelect.setFocus(true);
	}

	public void selectItemAfter(VContextMenuBasicItem item) {
		int index = menuItems.indexOf(item);

		index += 1;

		if (index >= menuItems.size()) {
			index = 0;
		}

		VContextMenuBasicItem itemToSelect = menuItems.get(index);
		itemToSelect.setFocus(true);
	}

	public void addMenuItem(VContextMenuBasicItem menuItem) {
		menuItem.setOwner(this);

		menuItems.add(menuItem);
		root.add(menuItem);
	}

	public void clearItems() {
		menuItems.clear();
		root.clear();
	}
}
