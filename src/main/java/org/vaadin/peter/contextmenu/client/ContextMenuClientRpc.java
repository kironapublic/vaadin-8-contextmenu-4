package org.vaadin.peter.contextmenu.client;

import com.vaadin.shared.communication.ClientRpc;

public interface ContextMenuClientRpc extends ClientRpc {

	public void showContextMenu(int x, int y);
}
