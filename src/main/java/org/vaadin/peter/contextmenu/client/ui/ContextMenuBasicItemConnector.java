package org.vaadin.peter.contextmenu.client.ui;

import org.vaadin.peter.contextmenu.ContextMenuBasicItem;

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentContainerConnector;
import com.vaadin.shared.ui.Connect;

@Connect(ContextMenuBasicItem.class)
public class ContextMenuBasicItemConnector extends
		AbstractComponentContainerConnector implements ClickHandler,
		MouseOverHandler, MouseOutHandler, KeyUpHandler {
	private static final long serialVersionUID = 8439116886277413949L;

	private ContextMenuItemServerRpc contextMenuRpc;

	private final Timer openTimer = new Timer() {
		@Override
		public void run() {
			onItemClicked();
		}
	};

	@Override
	protected void init() {
		super.init();

		getWidget().addClickHandler(this);
		getWidget().addMouseOverHandler(this);
		getWidget().addMouseOutHandler(this);
		getWidget().addKeyUpHandler(this);

		contextMenuRpc = RpcProxy.create(ContextMenuItemServerRpc.class, this);
	}

	@Override
	protected Widget createWidget() {
		return GWT.create(VContextMenuBasicItem.class);
	}

	@Override
	public VContextMenuBasicItem getWidget() {
		return (VContextMenuBasicItem) super.getWidget();
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
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		openTimer.cancel();

		if (isEnabled()) {
			getWidget().closeSiblingMenus();
			getWidget().setFocus(true);

			if (getWidget().hasSubMenu()) {
				openTimer.schedule(500);
			}
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		if (isEnabled()) {
			openTimer.cancel();

			if (getWidget().hasSubMenu()) {
				if (!getWidget().isSubmenuOpen()) {
					getWidget().onItemClicked();
					contextMenuRpc.itemClicked(false);
				}
			} else {
				boolean menuClosed = getWidget().onItemClicked();
				contextMenuRpc.itemClicked(menuClosed);
			}
		}
	}

	private void onLeftPressed() {
		if (isEnabled()) {
			getWidget().closeThisAndSelectParent();
		}
	}

	private void onRightPressed() {
		if (isEnabled()) {
			if (getWidget().hasSubMenu()) {
				onItemClicked();
			}
		}
	}

	private void onUpPressed() {
		if (isEnabled()) {
			getWidget().selectUpperSibling();
		}
	}

	private void onDownPressed() {
		if (isEnabled()) {
			getWidget().selectLowerSibling();
		}
	}

	private void onEnterPressed() {
		if (isEnabled()) {
			if (getWidget().hasSubMenu()) {
				if (!getWidget().isSubmenuOpen()) {
					getWidget().onItemClicked();
					contextMenuRpc.itemClicked(false);
				}
			} else {
				boolean menuClosed = getWidget().onItemClicked();
				contextMenuRpc.itemClicked(menuClosed);
			}
		}
	}

	private void onItemClicked() {
		boolean menuClosed = getWidget().onItemClicked();
		contextMenuRpc.itemClicked(menuClosed);
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);

		getWidget().setCaption(getState().caption);
		getWidget().removeIcon();

		// icon
	}

	@Override
	public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
		super.onConnectorHierarchyChange(event);

		getWidget().clearItems();

		for (ComponentConnector child : getChildComponents()) {
			getWidget().addSubMenuItem(
					(VContextMenuBasicItem) child.getWidget());
		}
	}

	@Override
	public void updateCaption(ComponentConnector connector) {
		// TODO Auto-generated method stub
	}
}
