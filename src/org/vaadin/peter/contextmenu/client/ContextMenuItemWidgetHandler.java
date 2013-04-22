package org.vaadin.peter.contextmenu.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Timer;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.RpcProxy;

/**
 * ContextMenuItemWidgetHandler is context menu item specific object that
 * handles the server communication when item is interacted with.
 * 
 * @author Peter Lehto / Vaadin Ltd
 * 
 */
public class ContextMenuItemWidgetHandler implements ClickHandler,
		MouseOverHandler, MouseOutHandler, KeyUpHandler {

	private ContextMenuServerRpc contextMenuRpc;

	private final Timer openTimer = new Timer() {
		@Override
		public void run() {
			onItemClicked();
		}
	};

	private ContextMenuItemWidget widget;

	public ContextMenuItemWidgetHandler(ContextMenuItemWidget widget,
			ServerConnector connector) {
		this.widget = widget;

		contextMenuRpc = RpcProxy.create(ContextMenuServerRpc.class, connector);
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		int keycode = event.getNativeEvent().getKeyCode();

		if (keycode == KeyCodes.KEY_LEFT) {
			onLeftPressed();
		} else if (keycode == KeyCodes.KEY_RIGHT) {
			onRightPressed();
		} else if (keycode == KeyCodes.KEY_UP) {
			onUpPressed();
		} else if (keycode == KeyCodes.KEY_DOWN) {
			onDownPressed();
		} else if (keycode == KeyCodes.KEY_ENTER) {
			onEnterPressed();
		}
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		openTimer.cancel();
		widget.setFocus(false);
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		openTimer.cancel();

		if (isEnabled()) {
			if (!widget.isSubmenuOpen()) {
				widget.closeSiblingMenus();
			}
			widget.setFocus(true);

			if (widget.hasSubMenu() && !widget.isSubmenuOpen()) {
				openTimer.schedule(500);
			}
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		if (isEnabled()) {
			openTimer.cancel();

			if (widget.hasSubMenu()) {
				if (!widget.isSubmenuOpen()) {
					widget.onItemClicked();
					contextMenuRpc.itemClicked(widget.getId(), false);
				}
			} else {
				boolean menuClosed = widget.onItemClicked();
				contextMenuRpc.itemClicked(widget.getId(), menuClosed);
			}
		}
	}

	private boolean isEnabled() {
		return widget.isEnabled();
	}

	private void onLeftPressed() {
		if (isEnabled()) {
			widget.closeThisAndSelectParent();
		}
	}

	private void onRightPressed() {
		if (isEnabled()) {
			if (widget.hasSubMenu()) {
				onItemClicked();
			}
		}
	}

	private void onUpPressed() {
		if (isEnabled()) {
			widget.selectUpperSibling();
		}
	}

	private void onDownPressed() {
		if (isEnabled()) {
			widget.selectLowerSibling();
		}
	}

	private void onEnterPressed() {
		if (isEnabled()) {
			if (widget.hasSubMenu()) {
				if (!widget.isSubmenuOpen()) {
					widget.onItemClicked();
					contextMenuRpc.itemClicked(widget.getId(), false);
				}
			} else {
				boolean menuClosed = widget.onItemClicked();
				contextMenuRpc.itemClicked(widget.getId(), menuClosed);
			}
		}
	}

	private void onItemClicked() {
		boolean menuClosed = widget.onItemClicked();
		contextMenuRpc.itemClicked(widget.getId(), menuClosed);
	}

}
