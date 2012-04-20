package org.vaadin.peter.contextmenu;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.vaadin.peter.contextmenu.ContextMenuItem.ContextMenuItemClickListener;

class ContextMenuListenerManager implements
		Iterable<ContextMenuItem.ContextMenuItemClickListener> {

	private final List<ContextMenuItem.ContextMenuItemClickListener> listeners;

	public ContextMenuListenerManager() {
		listeners = new LinkedList<ContextMenuItem.ContextMenuItemClickListener>();
	}

	public void addListener(
			ContextMenuItem.ContextMenuItemClickListener listener) {
		listeners.add(listener);
	}

	public void removeListener(
			ContextMenuItem.ContextMenuItemClickListener listener) {
		listeners.remove(listener);
	}

	public boolean hasListener(
			ContextMenuItem.ContextMenuItemClickListener listener) {
		return listeners.contains(listener);
	}

	@Override
	public Iterator<ContextMenuItemClickListener> iterator() {
		return listeners.iterator();
	}

	public void addAllListenersForItem(AbstractContextMenuItem item) {
		for (ContextMenuItemClickListener listener : listeners) {
			if (!item.hasListener(listener)) {
				item.addListenerRecursively(listener);
			}
		}
	}
}
