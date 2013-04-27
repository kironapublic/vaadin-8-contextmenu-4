package org.vaadin.peter.contextmenu.client;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.vaadin.client.ui.VOverlay;

/**
 * Menu is a visible item of ContextMenu component. For every child menu new
 * instance of Menu class will be instantiated.
 * 
 * @author Peter Lehto / IT Mill Oy Ltd
 */
class ContextMenuOverlay extends VOverlay {
	private final FlowPanel root;

	private final List<ContextMenuItemWidget> menuItems;

	private final CloseHandler<PopupPanel> closeHandler = new CloseHandler<PopupPanel>() {

		@Override
		public void onClose(CloseEvent<PopupPanel> event) {
			unfocusAll();
		}
	};

	public ContextMenuOverlay() {
		super(false, false, true);

		addCloseHandler(closeHandler);
		setStyleName("v-context-menu-container");

		root = new FlowPanel();
		root.setStyleName("v-context-menu");

		menuItems = new LinkedList<ContextMenuItemWidget>();

		add(root);
	}

	public boolean isSubmenuOpen() {
		for (ContextMenuItemWidget item : menuItems) {
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
		for (ContextMenuItemWidget item : menuItems) {
			item.setFocus(false);
		}
	}

	protected void normalizeItemWidths() {
		int widestItemWidth = getWidthOfWidestItem();

		for (ContextMenuItemWidget item : menuItems) {
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

		for (ContextMenuItemWidget item : menuItems) {
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

	public List<ContextMenuItemWidget> getMenuItems() {
		return menuItems;
	}

	/**
	 * @return number of visible items in this menu
	 */
	public int getNumberOfItems() {
		return this.menuItems.size();
	}

	public void openNextTo(ContextMenuItemWidget parentMenuItem) {
		int left = parentMenuItem.getAbsoluteLeft()
				+ parentMenuItem.getOffsetWidth();
		int top = parentMenuItem.getAbsoluteTop();

        showAt(left, top);
	}

	public void closeSubMenus() {
		for (ContextMenuItemWidget child : menuItems) {
			child.hideSubMenu();
		}
	}

	public void selectItemBefore(ContextMenuItemWidget item) {
		int index = menuItems.indexOf(item);

		index -= 1;

		if (index < 0) {
			index = menuItems.size() - 1;
		}

		ContextMenuItemWidget itemToSelect = menuItems.get(index);
		itemToSelect.setFocus(true);
	}

	public void selectItemAfter(ContextMenuItemWidget item) {
		int index = menuItems.indexOf(item);

		index += 1;

		if (index >= menuItems.size()) {
			index = 0;
		}

		ContextMenuItemWidget itemToSelect = menuItems.get(index);
		itemToSelect.setFocus(true);
	}

	public void addMenuItem(ContextMenuItemWidget menuItem) {
		menuItem.setOverlay(this);

		menuItems.add(menuItem);
		root.add(menuItem);
	}

	public void clearItems() {
		menuItems.clear();
		root.clear();
	}

    public void showAt(final int x, final int y) {
        setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            @Override
            public void setPosition(int offsetWidth, int offsetHeight) {
                int left = x + Window.getScrollLeft();
                int top = y + Window.getScrollTop();

                int exceedingWidth = (offsetWidth + left) - (Window.getClientWidth() + Window.getScrollLeft());
                if (exceedingWidth > 0) {
                    left -= exceedingWidth;
                    if (left < 0) {
                        left = 0;
                    }
                }

                int exceedingHeight = (offsetHeight + top) - (Window.getClientHeight() + Window.getScrollTop());
                if (exceedingHeight > 0) {
                    top -= exceedingHeight;
                    if (top < 0) {
                        top = 0;
                    }
                }

                setPopupPosition(left, top);
            }
        });

        normalizeItemWidths();
    }
}
