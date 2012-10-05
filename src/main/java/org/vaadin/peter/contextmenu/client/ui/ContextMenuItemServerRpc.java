package org.vaadin.peter.contextmenu.client.ui;

import com.vaadin.shared.communication.ServerRpc;

public interface ContextMenuItemServerRpc extends ServerRpc {

	/**
	 * Called by the client widget when context menu item is clicked
	 * 
	 * @param menuClosed
	 *            will be true if menu was closed after the click
	 */
	public void itemClicked(boolean menuClosed);
}
