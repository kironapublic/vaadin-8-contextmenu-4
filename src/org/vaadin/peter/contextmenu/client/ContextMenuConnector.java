package org.vaadin.peter.contextmenu.client;

import org.vaadin.peter.contextmenu.ContextMenu;
import org.vaadin.peter.contextmenu.client.ContextMenuState.ContextMenuItemState;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.Util;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.VScrollTable;
import com.vaadin.client.ui.VTree;
import com.vaadin.shared.ui.Connect;

/**
 * ContextMenuConnector is client side object that receives updates from server
 * and passes them to context menu client side widget. Connector is also
 * responsible for handling user interaction and communicating it back to
 * server.
 * 
 * @author Peter Lehto / Vaadin Ltd
 * 
 */
@Connect(ContextMenu.class)
public class ContextMenuConnector extends AbstractExtensionConnector {
	private static final long serialVersionUID = 3830712282306785118L;

	private ContextMenuWidget widget;

	private Widget extensionTarget;

	private ContextMenuServerRpc clientToServerRPC = RpcProxy.create(
			ContextMenuServerRpc.class, this);

	private CloseHandler<PopupPanel> contextMenuCloseHandler = new CloseHandler<PopupPanel>() {
		@Override
		public void onClose(CloseEvent<PopupPanel> popupPanelCloseEvent) {
			clientToServerRPC.contextMenuClosed();
		}
	};

	private final ContextMenuHandler contextMenuHandler = new ContextMenuHandler() {

		@Override
		public void onContextMenu(ContextMenuEvent event) {
			event.preventDefault();
			event.stopPropagation();

			EventTarget eventTarget = event.getNativeEvent().getEventTarget();

			ComponentConnector connector = Util.getConnectorForElement(
					getConnection(), getConnection().getUIConnector()
							.getWidget(), (Element) eventTarget.cast());

			Widget clickTargetWidget = connector.getWidget();

			if (extensionTarget.equals(clickTargetWidget)) {
				if (getState().isOpenAutomatically()) {
					widget.showContextMenu(event.getNativeEvent().getClientX(),
							event.getNativeEvent().getClientY());
				} else {
					clientToServerRPC.onContextMenuOpenRequested(event
							.getNativeEvent().getClientX(), event
							.getNativeEvent().getClientY(), connector
							.getConnectorId());
				}
			}
		}
	};

	@SuppressWarnings("serial")
	private ContextMenuClientRpc serverToClientRPC = new ContextMenuClientRpc() {

		@Override
		public void showContextMenu(int x, int y) {
			widget.showContextMenu(x, y);
		}
	};

	@Override
	protected void init() {
		widget = GWT.create(ContextMenuWidget.class);
		widget.addCloseHandler(contextMenuCloseHandler);
		registerRpc(ContextMenuClientRpc.class, serverToClientRPC);
	}

	@Override
	public ContextMenuState getState() {
		return (ContextMenuState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);

		widget.clearItems();

		for (ContextMenuItemState rootItem : getState().getRootItems()) {
			widget.addRootMenuItem(rootItem, this);
		}
	}

	@Override
	protected void extend(ServerConnector extensionTarget) {
		this.extensionTarget = ((ComponentConnector) extensionTarget)
				.getWidget();

		// Table and Tree are currently handled with their internal
		// ItemClickListeners as their current implementations are not too
		// extension friendly.
		if (this.extensionTarget instanceof VScrollTable) {
			return;
		} else if (this.extensionTarget instanceof VTree) {
			return;
		}

		this.extensionTarget.addDomHandler(contextMenuHandler,
				ContextMenuEvent.getType());
	}
}
