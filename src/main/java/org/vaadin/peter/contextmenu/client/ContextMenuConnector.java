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
import com.vaadin.client.ui.AbstractLayoutConnector;
import com.vaadin.client.ui.VScrollTable;
import com.vaadin.client.ui.VTree;
import com.vaadin.client.ui.table.TableConnector;
import com.vaadin.client.ui.tree.TreeConnector;
import com.vaadin.shared.ui.Connect;

@Connect(ContextMenu.class)
public class ContextMenuConnector extends AbstractExtensionConnector {
	private static final long serialVersionUID = 3830712282306785118L;

	private ContextMenuWidget widget;

	private Widget extensionTarget;

	private final ContextMenuHandler layoutClickHandler = new ContextMenuHandler() {

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
				widget.showContextMenu(event.getNativeEvent().getClientX(),
						event.getNativeEvent().getClientY());
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
		widget.addDomHandler(layoutClickHandler, ContextMenuEvent.getType());
	}

	private void extendTree(VTree widget) {
		// Will be implemented when tree supports easier extension
	}

	private void extendTable(VScrollTable widget) {
		// Will be implemented when table supports easier extension
	}
}
