package org.vaadin.peter.contextmenu.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * Client side implementation for ContextMenu component
 * 
 * @author Peter Lehto / Vaadin Ltd
 */
public class VContextMenuBasic extends Widget {
	private final VBasicMenu rootMenu;

	private final NativePreviewHandler nativeEventHandler = new NativePreviewHandler() {

		@Override
		public void onPreviewNativeEvent(NativePreviewEvent event) {
			if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
				// Always close the context menu on esc, no matter the focus
				hide();
			}

			Event nativeEvent = Event.as(event.getNativeEvent());
			boolean targetsContextMenu = eventTargetContextMenu(nativeEvent);

			if (!targetsContextMenu) {
				int type = nativeEvent.getTypeInt();
				switch (type) {
				case Event.ONMOUSEDOWN: {
					hide();
				}
				}
			}
		}
	};

	public VContextMenuBasic() {
		Element element = DOM.createDiv();
		setElement(element);

		Event.addNativePreviewHandler(nativeEventHandler);

		rootMenu = new VBasicMenu();
	}

	protected boolean eventTargetContextMenu(Event nativeEvent) {
		for (VContextMenuBasicItem item : rootMenu.getMenuItems()) {
			if (item.eventTargetsPopup(nativeEvent)) {
				return true;
			}
		}

		return false;
	}

	protected boolean isShowing() {
		return rootMenu.isShowing();
	}

	public void hide() {
		rootMenu.hide();
	}

	public void addRootMenuItem(VContextMenuBasicItem rootItem) {
		rootItem.setRootComponent(this);
		rootItem.setParentItem(null);

		rootMenu.addMenuItem(rootItem);
	}

	public void clearItems() {
		rootMenu.clearItems();
	}

	public void showContextMenu(int rootMenuX, int rootMenuY) {
		rootMenuX += Window.getScrollLeft();
		rootMenuY += Window.getScrollTop();

		rootMenu.setPopupPosition(rootMenuX, rootMenuY);
		rootMenu.show();

		rootMenu.normalizeItemWidths();
	}
}
