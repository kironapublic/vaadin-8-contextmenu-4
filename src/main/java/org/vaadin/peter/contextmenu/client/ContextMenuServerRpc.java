package org.vaadin.peter.contextmenu.client;

import com.vaadin.shared.communication.ServerRpc;

public interface ContextMenuServerRpc extends ServerRpc {

	/**
	 * Called by the client widget when context menu item is clicked
	 * 
	 * @param itemId
	 *            id of the clicked item
	 * 
	 * @param menuClosed
	 *            will be true if menu was closed after the click
	 */
	public void itemClicked(String itemId, boolean menuClosed);

	/**
	 * Called by the client side when context menu is about to be opened.
	 */
	public void onContextMenuOpen();
}
