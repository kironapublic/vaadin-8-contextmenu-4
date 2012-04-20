package org.vaadin.peter.contextmenu.client.ui;

import org.vaadin.peter.contextmenu.ContextMenuBasic;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ConnectorHierarchyChangeEvent;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentContainerConnector;
import com.vaadin.terminal.gwt.client.ui.Connect;

@Connect(ContextMenuBasic.class)
public class ContextMenuBasicConnector extends
		AbstractComponentContainerConnector {
	private static final long serialVersionUID = 3830712282306785118L;

	@Override
	protected Widget createWidget() {
		return GWT.create(VContextMenuBasic.class);
	}

	@Override
	public VContextMenuBasic getWidget() {
		return (VContextMenuBasic) super.getWidget();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);

		ContextMenuState state = (ContextMenuState) getState();

		if (state.isShowing()) {
			getWidget().showContextMenu(state.getRootMenuX(),
					state.getRootMenuY());
		} else {
			getWidget().hide();
		}
	}

	@Override
	public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
		super.onConnectorHierarchyChange(event);

		getWidget().clearItems();

		for (ComponentConnector child : getChildren()) {
			getWidget().addRootMenuItem(
					(VContextMenuBasicItem) child.getWidget());
		}
	}

	@Override
	public void updateCaption(ComponentConnector connector) {
		// NOP
	}

	public void menuItemClicked(
			ContextMenuBasicItemConnector contextMenuBasicItemConnector) {
		// TODO Auto-generated method stub

	}
}
