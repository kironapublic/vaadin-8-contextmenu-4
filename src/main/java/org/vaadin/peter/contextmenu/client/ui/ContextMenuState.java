package org.vaadin.peter.contextmenu.client.ui;

import com.vaadin.shared.ComponentState;

public class ContextMenuState extends ComponentState {
	private static final long serialVersionUID = -247856391284942254L;

	private int rootMenuX;
	private int rootMenuY;

	private boolean showing;

	public void setRootMenuX(int rootMenuX) {
		this.rootMenuX = rootMenuX;
	}

	public void setRootMenuY(int rootMenuY) {
		this.rootMenuY = rootMenuY;
	}

	public int getRootMenuX() {
		return rootMenuX;
	}

	public int getRootMenuY() {
		return rootMenuY;
	}

	public boolean isShowing() {
		return showing;
	}

	public void setShowing(boolean showing) {
		this.showing = showing;
	}
}
