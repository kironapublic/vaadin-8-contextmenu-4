package org.vaadin.peter.contextmenu.client;

import org.vaadin.peter.contextmenu.ContextMenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentContainerConnector;
import com.vaadin.shared.ui.Connect;

@Connect(ContextMenu.class)
public class ContextMenuConnector extends
		AbstractComponentContainerConnector {
	private static final long serialVersionUID = 3830712282306785118L;

	@Override
	protected Widget createWidget() {
		return GWT.create(ContextMenuWidget.class);
	}

	@Override
	public ContextMenuWidget getWidget() {
		return (ContextMenuWidget) super.getWidget();
	}

	@Override
	public ContextMenuState getState() {
		return (ContextMenuState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);

		ContextMenuState state = getState();

		if (state.isShowing()) {
			getWidget().showContextMenu(state.getRootMenuX(),
					state.getRootMenuY());
		} else {
			getWidget().hide();
		}
	}

	@Override
	public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {

		getWidget().clearItems();

		for (ComponentConnector child : getChildComponents()) {
			getWidget().addRootMenuItem(
					(ContextMenuItemWidget) child.getWidget());
		}
	}

	@Override
	public void updateCaption(ComponentConnector connector) {
		// NOP
	}

	public void menuItemClicked(
			ContextMenuItemConnector contextMenuBasicItemConnector) {
		// TODO Auto-generated method stub

	}
}
