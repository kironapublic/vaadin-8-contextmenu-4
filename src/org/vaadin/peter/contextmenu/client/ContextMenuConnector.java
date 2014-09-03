package org.vaadin.peter.contextmenu.client;

import org.vaadin.peter.contextmenu.ContextMenu;
import org.vaadin.peter.contextmenu.client.ContextMenuState.ContextMenuItemState;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.Util;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.AbstractComponentConnector;
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

            ApplicationConnection connection = getConnection();
            ComponentConnector connector =
                    Util.getConnectorForElement(connection, getConnection().getUIConnector().getWidget(),
                    (Element) eventTarget.cast());

            if (connector == null) {
                    connector = Util.getConnectorForElement(connection, RootPanel.get(), (Element) eventTarget.cast());
            }

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

	private HandlerRegistration contextMenuCloseHandlerRegistration;

	private HandlerRegistration contextMenuHandlerRegistration;

	@SuppressWarnings("serial")
	private ContextMenuClientRpc serverToClientRPC = new ContextMenuClientRpc() {

		@Override
		public void showContextMenu(int x, int y) {
			widget.showContextMenu(x, y);
		}

		@Override
		public void showContextMenuRelativeTo(String connectorId) {
			ServerConnector connector = ConnectorMap.get(getConnection())
					.getConnector(connectorId);

			if (connector instanceof AbstractComponentConnector) {
				AbstractComponentConnector componentConnector = (AbstractComponentConnector) connector;
				componentConnector.getWidget();

				widget.showContextMenu(componentConnector.getWidget());
			}
		}

		@Override
		public void hide() {
			widget.hide();
		}

	};

	@Override
	protected void init() {
		widget = GWT.create(ContextMenuWidget.class);
		contextMenuCloseHandlerRegistration = widget
				.addCloseHandler(contextMenuCloseHandler);
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
		widget.setHideAutomatically(getState().isHideAutomatically());

		for (ContextMenuItemState rootItem : getState().getRootItems()) {
			widget.addRootMenuItem(rootItem, this);
		}
	}

	@Override
	protected void extend(ServerConnector extensionTarget) {
		this.extensionTarget = ((ComponentConnector) extensionTarget)
				.getWidget();

		widget.setExtensionTarget(this.extensionTarget);

		contextMenuHandlerRegistration = this.extensionTarget.addDomHandler(
				contextMenuHandler, ContextMenuEvent.getType());
	}

	@Override
	public void onUnregister() {
		contextMenuCloseHandlerRegistration.removeHandler();
		contextMenuHandlerRegistration.removeHandler();

		widget.unregister();

		super.onUnregister();
	}
}
