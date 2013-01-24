package org.vaadin.peter.contextmenu.client;

import com.vaadin.shared.communication.ClientRpc;

/**
 * Server to client RPC communication.
 * 
 * @author Peter Lehto / Vaadin Ltd
 * 
 */
public interface ContextMenuClientRpc extends ClientRpc {

	/**
	 * Sends request to client widget to open context menu to given position.
	 * 
	 * @param x
	 * @param y
	 */
	public void showContextMenu(int x, int y);
}
