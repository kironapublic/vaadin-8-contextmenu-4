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

	/**
	 * Sends request to client widget to open context menu relative to component
	 * identified by given connectorId. (Method is on purpose with different
	 * name from showContextMenu as overloading does not work properly in
	 * javascript environment.)
	 * 
	 * @param connectorId
	 */
	public void showContextMenuRelativeTo(String connectorId);

	/**
	 * Sends request to client widget to close context menu
	 */
	public void hide();

}
