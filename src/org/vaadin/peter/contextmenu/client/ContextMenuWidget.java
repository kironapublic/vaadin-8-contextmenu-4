package org.vaadin.peter.contextmenu.client;

import java.util.Set;

import org.vaadin.peter.contextmenu.client.ContextMenuState.ContextMenuItemState;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Client side implementation for ContextMenu component
 * 
 * @author Peter Lehto / Vaadin Ltd
 */
public class ContextMenuWidget extends Widget {
	private final ContextMenuOverlay menuOverlay;

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
					if (isHideAutomatically()) {
						hide();
					}
				}
				}
			}
		}
	};

	private final HandlerRegistration nativeEventHandlerRegistration;

	private boolean hideAutomatically;

	private Widget extensionTarget;

	public ContextMenuWidget() {
		Element element = DOM.createDiv();
		setElement(element);

		nativeEventHandlerRegistration = Event
				.addNativePreviewHandler(nativeEventHandler);

		menuOverlay = new ContextMenuOverlay();
	}

	protected boolean eventTargetContextMenu(Event nativeEvent) {
		for (ContextMenuItemWidget item : menuOverlay.getMenuItems()) {
			if (item.eventTargetsPopup(nativeEvent)) {
				return true;
			}
		}

		return false;
	}

	protected boolean isShowing() {
		return menuOverlay.isShowing();
	}

	public void hide() {
		menuOverlay.hide();
	}

	/**
	 * Adds new item as context menu root item.
	 * 
	 * @param rootItem
	 * @param connector
	 */
	public void addRootMenuItem(ContextMenuItemState rootItem,
			ContextMenuConnector connector) {
		ContextMenuItemWidget itemWidget = createEmptyItemWidget(rootItem.id,
				rootItem.caption, connector);
		itemWidget.setEnabled(rootItem.enabled);
		itemWidget.setSeparatorVisible(rootItem.separator);

		setStyleNames(itemWidget, rootItem.getStyles());

		menuOverlay.addMenuItem(itemWidget);

		for (ContextMenuItemState childState : rootItem.getChildren()) {
			createSubMenu(itemWidget, childState, connector);
		}
	}

	private void setStyleNames(ContextMenuItemWidget item, Set<String> styles) {
		for (String style : styles) {
			item.addStyleName(style);
		}
	}

	/**
	 * Creates new empty menu item
	 * 
	 * @param id
	 * @param caption
	 * @param contextMenuConnector
	 * @return
	 */
	private ContextMenuItemWidget createEmptyItemWidget(String id,
			String caption, ContextMenuConnector contextMenuConnector) {
		ContextMenuItemWidget widget = GWT.create(ContextMenuItemWidget.class);
		widget.setId(id);
		widget.setCaption(caption);

		widget.setIcon(contextMenuConnector.getResourceUrl(id));

		ContextMenuItemWidgetHandler handler = new ContextMenuItemWidgetHandler(
				widget, contextMenuConnector);
		widget.addClickHandler(handler);
		widget.addMouseOutHandler(handler);
		widget.addMouseOverHandler(handler);
		widget.addKeyUpHandler(handler);
		widget.setRootComponent(this);

		return widget;
	}

	private void createSubMenu(ContextMenuItemWidget parentWidget,
			ContextMenuItemState childState, ContextMenuConnector connector) {
		ContextMenuItemWidget childWidget = createEmptyItemWidget(
				childState.id, childState.caption, connector);
		childWidget.setEnabled(childState.enabled);
		childWidget.setSeparatorVisible(childState.separator);
		setStyleNames(childWidget, childState.getStyles());
		parentWidget.addSubMenuItem(childWidget);

		for (ContextMenuItemState child : childState.getChildren()) {
			createSubMenu(childWidget, child, connector);
		}
	}

	public void clearItems() {
		menuOverlay.clearItems();
	}

	public void showContextMenu(int rootMenuX, int rootMenuY) {
		menuOverlay.showAt(rootMenuX, rootMenuY);
	}

	public void showContextMenu(Widget widget) {
		menuOverlay.showRelativeTo(widget);
	}

	public HandlerRegistration addCloseHandler(
			CloseHandler<PopupPanel> popupCloseHandler) {
		return menuOverlay.addCloseHandler(popupCloseHandler);
	}

	public void setHideAutomatically(boolean hideAutomatically) {
		this.hideAutomatically = hideAutomatically;
	}

	public boolean isHideAutomatically() {
		return hideAutomatically;
	}

	public void unregister() {
		nativeEventHandlerRegistration.removeHandler();
		menuOverlay.unregister();
	}

	public void setExtensionTarget(Widget extensionTarget) {
		this.extensionTarget = extensionTarget;
		menuOverlay.setOwner(extensionTarget);
	}

	public Widget getExtensionTarget() {
		return extensionTarget;
	}
}
