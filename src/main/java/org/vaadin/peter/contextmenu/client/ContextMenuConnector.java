package org.vaadin.peter.contextmenu.client;

import org.vaadin.peter.contextmenu.ContextMenu;
import org.vaadin.peter.contextmenu.client.ContextMenuState.ContextMenuItemState;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.AbstractLayoutConnector;
import com.vaadin.client.ui.table.TableConnector;
import com.vaadin.client.ui.table.VScrollTable;
import com.vaadin.client.ui.tree.TreeConnector;
import com.vaadin.client.ui.tree.VTree;
import com.vaadin.shared.ui.Connect;

@Connect(ContextMenu.class)
public class ContextMenuConnector extends AbstractExtensionConnector {
	private static final long serialVersionUID = 3830712282306785118L;

	private ContextMenuWidget widget;

	private final ClickHandler layoutClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent clickEvent) {
			showContextMenu(clickEvent.getClientX(), clickEvent.getClientY());
		}
	};

	@Override
	protected void init() {
		widget = GWT.create(ContextMenuWidget.class);
	}

	@Override
	public ContextMenuState getState() {
		return (ContextMenuState) super.getState();
	}

	protected void showContextMenu(int clientX, int clientY) {
		widget.showContextMenu(clientX, clientY);
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
		if (extensionTarget instanceof TableConnector) {
			extendTable(((TableConnector) extensionTarget).getWidget());
		} else if (extensionTarget instanceof TreeConnector) {
			extendTree(((TreeConnector) extensionTarget).getWidget());
		} else if (extensionTarget instanceof AbstractLayoutConnector) {
			extendLayout(((AbstractLayoutConnector) extensionTarget)
					.getWidget());
		}
	}

	private void extendLayout(Widget widget) {
		widget.addDomHandler(layoutClickHandler, ClickEvent.getType());
	}

	private void extendTree(VTree widget) {

	}

	private void extendTable(VScrollTable widget) {

	}

}
