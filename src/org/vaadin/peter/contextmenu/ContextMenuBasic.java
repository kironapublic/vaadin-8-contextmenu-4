package org.vaadin.peter.contextmenu;


public class ContextMenuBasic extends AbstractContextMenu {
	private static final long serialVersionUID = 863617888835523888L;

	@Override
	public Class<?> getContextMenuItemType() {
		return ContextMenuBasicItem.class;
	}
}
