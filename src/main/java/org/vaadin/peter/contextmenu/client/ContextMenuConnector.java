package org.vaadin.peter.contextmenu.client;

import org.vaadin.peter.contextmenu.ContextMenu;
import org.vaadin.peter.contextmenu.client.ContextMenuState.ContextMenuItemState;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.Util;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.VScrollTable;
import com.vaadin.shared.ui.Connect;

@Connect(ContextMenu.class)
public class ContextMenuConnector extends AbstractExtensionConnector {
	private static final long serialVersionUID = 3830712282306785118L;

	private ContextMenuWidget widget;

	private Widget extensionTarget;

	private final ContextMenuHandler contextMenuHandler = new ContextMenuHandler() {

		@Override
		public void onContextMenu(ContextMenuEvent event) {
			event.preventDefault();
			event.stopPropagation();

			EventTarget eventTarget = event.getNativeEvent().getEventTarget();

			Widget clickTargetWidget = Util.getConnectorForElement(
					getConnection(),
					getConnection().getUIConnector().getWidget(),
					(Element) eventTarget.cast()).getWidget();

			if (extensionTarget.equals(clickTargetWidget)) {
				if (getState().isOpenAutomatically()) {
					widget.showContextMenu(event.getNativeEvent().getClientX(),
							event.getNativeEvent().getClientY());
				} else {

				}
			}
		}
	};

	private ContextMenuClientRpc clientRpc = new ContextMenuClientRpc() {

		@Override
		public void showContextMenu(int x, int y) {
			widget.showContextMenu(x, y);
		}
	};

	@Override
	protected void init() {
		widget = GWT.create(ContextMenuWidget.class);
		registerRpc(ContextMenuClientRpc.class, clientRpc);
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

		if (this.extensionTarget instanceof VScrollTable) {
			// Don't extend table because table is handled by different click
			// listeners for now
			return;
		}

		this.extensionTarget.addDomHandler(contextMenuHandler,
				ContextMenuEvent.getType());
	}
}
