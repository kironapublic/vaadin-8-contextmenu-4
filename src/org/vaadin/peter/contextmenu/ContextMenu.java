package org.vaadin.peter.contextmenu;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

import org.vaadin.peter.contextmenu.client.ContextMenuClientRpc;
import org.vaadin.peter.contextmenu.client.ContextMenuServerRpc;
import org.vaadin.peter.contextmenu.client.ContextMenuState;
import org.vaadin.peter.contextmenu.client.ContextMenuState.ContextMenuItemState;

import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.Resource;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.ui.Component;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.FooterClickEvent;
import com.vaadin.v7.ui.Table.FooterClickListener;
import com.vaadin.v7.ui.Table.HeaderClickEvent;
import com.vaadin.v7.ui.Table.HeaderClickListener;
import com.vaadin.v7.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.util.ReflectTools;

/**
 * ContextMenu is an extension which can be attached to any Vaadin component to
 * display a popup context menu. Most useful the menu is when attached for
 * example to Tree or Table which support item and property based context menu
 * detection.
 * 
 * @author Peter / Vaadin
 */
@SuppressWarnings("deprecation")
public class ContextMenu extends AbstractExtension {
	private static final long serialVersionUID = 4275181115413786498L;

	private final Map<String, ContextMenuItem> items;

	private final ContextMenuServerRpc serverRPC = new ContextMenuServerRpc() {
		private static final long serialVersionUID = 5622864428554337992L;

		@Override
		public void itemClicked(String itemId, boolean menuClosed) {
			ContextMenuItem item = items.get(itemId);
			if (item == null) {
				return;
			}

			item.notifyClickListeners();
			fireEvent(new ContextMenuItemClickEvent(item));
		}

		@Override
		public void onContextMenuOpenRequested(int x, int y, String connectorId) {
			fireEvent(new ContextMenuOpenedOnComponentEvent(ContextMenu.this,
					x, y, (Component) UI.getCurrent().getConnectorTracker()
							.getConnector(connectorId)));
		}

		@Override
		public void contextMenuClosed() {
			fireEvent(new ContextMenuClosedEvent(ContextMenu.this));
		}
	};

	public ContextMenu() {
		registerRpc(serverRPC);

		items = new HashMap<String, ContextMenu.ContextMenuItem>();

		setOpenAutomatically(true);
		setHideAutomatically(true);
	}

	protected String getNextId() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Enables or disables open automatically feature. If open automatically is
	 * on, it means that context menu will always be opened when it's host
	 * component is right clicked. This will happen on client side without
	 * server round trip. If automatic opening is turned off, context menu will
	 * only open when server side open(x, y) is called. If automatic opening is
	 * disabled you will need a listener implementation for context menu that is
	 * called upon client side click event. Another option is to extend context
	 * menu and handle the right clicking internally with case specific listener
	 * implementation and inside it call open(x, y) method.
	 * 
	 * @param openAutomatically
	 */
	public void setOpenAutomatically(boolean openAutomatically) {
		getState().setOpenAutomatically(openAutomatically);
	}

	/**
	 * @return true if open automatically is on. If open automatically is on, it
	 *         means that context menu will always be opened when it's host
	 *         component is right clicked. If automatic opening is turned off,
	 *         context menu will only open when server side open(x, y) is
	 *         called. Automatic opening avoid having to make server roundtrip
	 *         whereas "manual" opening allows to have logic in menu before
	 *         opening it.
	 */
	public boolean isOpenAutomatically() {
		return getState().isOpenAutomatically();
	}

	/**
	 * Sets menu to hide automatically after mouse cliks on menu items or area
	 * off the menu. If automatic hiding is disabled menu will stay open as long
	 * as hide is called from the server side.
	 * 
	 * @param hideAutomatically
	 */
	public void setHideAutomatically(boolean hideAutomatically) {
		getState().setHideAutomatically(hideAutomatically);
	}

	/**
	 * @return true if context menu is hiding automatically after clicks, false
	 *         otherwise.
	 */
	public boolean isHideAutomatically() {
		return getState().isHideAutomatically();
	}

	/**
	 * Adds new item to context menu root with given caption.
	 * 
	 * @param caption
	 * @return reference to newly added item
	 */
	public ContextMenuItem addItem(String caption) {
		ContextMenuItemState itemState = getState().addChild(caption,
				getNextId());

		ContextMenuItem item = new ContextMenuItem(null, itemState);
		items.put(itemState.id, item);

		return item;
	}

	/**
	 * Adds new item to context menu root with given icon without caption.
	 * 
	 * @param icon
	 * @return reference to newly added item
	 */
	public ContextMenuItem addItem(Resource icon) {
		ContextMenuItem item = addItem("");
		item.setIcon(icon);
		return item;
	}

	/**
	 * Adds new item to context menu root with given caption and icon.
	 * 
	 * @param caption
	 * @param icon
	 * @return reference to newly added item
	 */
	public ContextMenuItem addItem(String caption, Resource icon) {
		ContextMenuItem item = addItem(caption);
		item.setIcon(icon);
		return item;
	}

	/**
	 * Removes given context menu item from the context menu. The given item can
	 * be a root item or leaf item or anything in between. If given given is not
	 * found from the context menu structure, this method has no effect.
	 * 
	 * @param contextMenuItem
	 */
	public void removeItem(ContextMenuItem contextMenuItem) {
		if (!hasMenuItem(contextMenuItem)) {
			return;
		}

		if (contextMenuItem.isRootItem()) {
			getState().getRootItems().remove(contextMenuItem.state);
		} else {
			ContextMenuItem parent = contextMenuItem.getParent();
			parent.state.getChildren().remove(contextMenuItem.state);
		}

		Set<ContextMenuItem> children = contextMenuItem.getAllChildren();

		items.remove(contextMenuItem.state.id);

		for (ContextMenuItem child : children) {
			items.remove(child.state.id);
		}

		markAsDirty();
	}

	private boolean hasMenuItem(ContextMenuItem contextMenuItem) {
		return items.containsKey(contextMenuItem.state.id);
	}

	/**
	 * Removes all items from the context menu.
	 */
	public void removeAllItems() {
		items.clear();
		getState().getRootItems().clear();
	}

	/**
	 * Assigns this as the context menu of given table.
	 * 
	 * @param table
	 */
	public void setAsTableContextMenu(final Table table) {
		extend(table);

		setOpenAutomatically(false);

		table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			private static final long serialVersionUID = -348059189217149508L;

			@Override
			public void itemClick(ItemClickEvent event) {
				if (event.getButton() == MouseButton.RIGHT) {
					fireEvent(new ContextMenuOpenedOnTableRowEvent(
							ContextMenu.this, table, event.getItemId(), event
									.getPropertyId()));
					open(event.getClientX(), event.getClientY());
				}
			}
		});

		table.addHeaderClickListener(new HeaderClickListener() {
			private static final long serialVersionUID = -5880755689414670581L;

			@Override
			public void headerClick(HeaderClickEvent event) {
				if (event.getButton() == MouseButton.RIGHT) {
					fireEvent(new ContextMenuOpenedOnTableHeaderEvent(
							ContextMenu.this, table, event.getPropertyId()));
					open(event.getClientX(), event.getClientY());
				}
			}
		});

		table.addFooterClickListener(new FooterClickListener() {
			private static final long serialVersionUID = 2884227013964132482L;

			@Override
			public void footerClick(FooterClickEvent event) {
				if (event.getButton() == MouseButton.RIGHT) {
					fireEvent(new ContextMenuOpenedOnTableHeaderEvent(
							ContextMenu.this, table, event.getPropertyId()));
					open(event.getClientX(), event.getClientY());
				}
			}
		});
	}

	/**
	 * Assigns this as context menu of given tree.
	 * 
	 * @param tree
	 */
	public void setAsTreeContextMenu(final Tree tree) {
		extend(tree);

		setOpenAutomatically(false);

		tree.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 338499886052623304L;

			@Override
			public void itemClick(ItemClickEvent event) {
				if (event.getButton() == MouseButton.RIGHT) {
					fireEvent(new ContextMenuOpenedOnTreeItemEvent(
							ContextMenu.this, tree, event.getItemId()));
					open(event.getClientX(), event.getClientY());
				}
			}
		});
	}

	/**
	 * Assigns this as context menu of given component which will react to right
	 * mouse button click.
	 * 
	 * @param component
	 */
	public void setAsContextMenuOf(AbstractClientConnector component) {
		if (component instanceof Table) {
			setAsTableContextMenu((Table) component);
		} else if (component instanceof Tree) {
			setAsTreeContextMenu((Tree) component);
		} else {
			super.extend(component);
		}
	}

	/**
	 * Opens the context menu to given coordinates. ContextMenu must extend
	 * component before calling this method. This method is only intended for
	 * opening the context menu from server side when using
	 * {@link #ContextMenuOpenedListener.ComponentListener}
	 * 
	 * @param x
	 * @param y
	 */
	public void open(int x, int y) {
		getRpcProxy(ContextMenuClientRpc.class).showContextMenu(x, y);
	}

	/**
	 * 
	 * @param component
	 */
	public void open(Component component) {
		getRpcProxy(ContextMenuClientRpc.class).showContextMenuRelativeTo(
				component.getConnectorId());
	}

	/**
	 * Closes the context menu from server side
	 */
	public void hide() {
		getRpcProxy(ContextMenuClientRpc.class).hide();
	}

	@Override
	protected ContextMenuState getState() {
		return (ContextMenuState) super.getState();
	}

	/**
	 * Adds click listener to context menu. This listener will be invoked when
	 * any of the menu items in this menu are clicked.
	 * 
	 * @param clickListener
	 */
	public void addItemClickListener(
			ContextMenu.ContextMenuItemClickListener clickListener) {
		addListener(ContextMenuItemClickEvent.class, clickListener,
				ContextMenuItemClickListener.ITEM_CLICK_METHOD);
	}

	/**
	 * Adds listener that will be invoked when context menu is opened from
	 * com.vaadin.ui.Table component.
	 * 
	 * @param contextMenuTableListener
	 */
	public void addContextMenuTableListener(
			ContextMenu.ContextMenuOpenedListener.TableListener contextMenuTableListener) {
		addListener(
				ContextMenuOpenedOnTableRowEvent.class,
				contextMenuTableListener,
				ContextMenuOpenedListener.TableListener.MENU_OPENED_FROM_TABLE_ROW_METHOD);
		addListener(
				ContextMenuOpenedOnTableHeaderEvent.class,
				contextMenuTableListener,
				ContextMenuOpenedListener.TableListener.MENU_OPENED_FROM_TABLE_HEADER_METHOD);
		addListener(
				ContextMenuOpenedOnTableFooterEvent.class,
				contextMenuTableListener,
				ContextMenuOpenedListener.TableListener.MENU_OPENED_FROM_TABLE_FOOTER_METHOD);
	}

	/**
	 * Adds listener that will be invoked when context menu is openef from
	 * com.vaadin.ui.Tree component.
	 * 
	 * @param contextMenuTreeListener
	 */
	public void addContextMenuTreeListener(
			ContextMenu.ContextMenuOpenedListener.TreeListener contextMenuTreeListener) {
		addListener(
				ContextMenuOpenedOnTreeItemEvent.class,
				contextMenuTreeListener,
				ContextMenuOpenedListener.TreeListener.MENU_OPENED_FROM_TREE_ITEM_METHOD);
	}

	/**
	 * Adds listener that will be invoked when context menu is closed.
	 * 
	 * @param contextMenuClosedListener
	 */
	public void addContextMenuCloseListener(
			ContextMenuClosedListener contextMenuClosedListener) {
		addListener(ContextMenuClosedEvent.class, contextMenuClosedListener,
				ContextMenuClosedListener.MENU_CLOSED);
	}

	/**
	 * Adds listener that will be invoked when context menu is opened from the
	 * component to which it's assigned to.
	 * 
	 * @param contextMenuComponentListener
	 */
	public void addContextMenuComponentListener(
			ContextMenu.ContextMenuOpenedListener.ComponentListener contextMenuComponentListener) {
		addListener(
				ContextMenuOpenedOnComponentEvent.class,
				contextMenuComponentListener,
				ContextMenuOpenedListener.ComponentListener.MENU_OPENED_FROM_COMPONENT);
	}

	/**
	 * ContextMenuItem represents one clickable item in the context menu. Item
	 * may have sub items.
	 * 
	 * @author Peter Lehto / Vaadin Ltd
	 * 
	 */
	public class ContextMenuItem implements Serializable {
		private static final long serialVersionUID = -6514832427611690050L;

		private ContextMenuItem parent;
		private final ContextMenuItemState state;

		private final List<ContextMenu.ContextMenuItemClickListener> clickListeners;

		private Object data;

		protected ContextMenuItem(ContextMenuItem parent,
				ContextMenuItemState itemState) {
			this.parent = parent;

			if (itemState == null) {
				throw new NullPointerException(
						"Context menu item state must not be null");
			}

			clickListeners = new ArrayList<ContextMenu.ContextMenuItemClickListener>();
			this.state = itemState;
		}

		protected Set<ContextMenuItem> getAllChildren() {
			Set<ContextMenuItem> children = new HashSet<ContextMenu.ContextMenuItem>();

			for (ContextMenuItemState childState : state.getChildren()) {
				ContextMenuItem child = items.get(childState.id);
				children.add(child);
				children.addAll(child.getAllChildren());
			}

			return children;
		}

		/**
		 * @return parent item of this menu item. Null if this item is a root
		 *         item.
		 */
		protected ContextMenuItem getParent() {
			return parent;
		}

		protected void notifyClickListeners() {
			for (ContextMenu.ContextMenuItemClickListener clickListener : clickListeners) {
				clickListener
						.contextMenuItemClicked(new ContextMenuItemClickEvent(
								this));
			}
		}

		/**
		 * Associates given object with this menu item. Given object can be
		 * whatever application specific if necessary.
		 * 
		 * @param data
		 */
		public void setData(Object data) {
			this.data = data;
		}

		/**
		 * @return Object associated with ContextMenuItem.
		 */
		public Object getData() {
			return data;
		}

		/**
		 * Adds new item as this item's sub item with given caption
		 * 
		 * @param caption
		 * @return reference to newly created item.
		 */
		public ContextMenuItem addItem(String caption) {
			ContextMenuItemState childItemState = state.addChild(caption,
					getNextId());
			ContextMenuItem item = new ContextMenuItem(this, childItemState);

			items.put(childItemState.id, item);
			markAsDirty();
			return item;
		}

		/**
		 * Adds new item as this item's sub item with given icon
		 * 
		 * @param icon
		 * @return reference to newly added item
		 */
		public ContextMenuItem addItem(Resource icon) {
			ContextMenuItem item = this.addItem("");
			item.setIcon(icon);

			return item;
		}

		/**
		 * Adds new item as this item's sub item with given caption and icon
		 * 
		 * @param caption
		 * @param icon
		 * @return reference to newly added item
		 */
		public ContextMenuItem addItem(String caption, Resource icon) {
			ContextMenuItem item = this.addItem(caption);
			item.setIcon(icon);

			return item;
		}

		/**
		 * Sets given resource as icon of this menu item.
		 * 
		 * @param icon
		 */
		public void setIcon(Resource icon) {
			setResource(state.id, icon);
		}

		/**
		 * @return current icon
		 */
		public Resource getIcon() {
			return getResource(state.id);
		}

		/**
		 * Sets or disables separator line under this item
		 * 
		 * @param visible
		 */
		public void setSeparatorVisible(boolean separatorVisible) {
			state.separator = separatorVisible;
			markAsDirty();
		}

		/**
		 * @return true if separator line is visible after this item, false
		 *         otherwise
		 */
		public boolean hasSeparator() {
			return state.separator;
		}

		/**
		 * Enables or disables this menu item
		 * 
		 * @param enabled
		 */
		public void setEnabled(boolean enabled) {
			state.enabled = enabled;
			markAsDirty();
		}

		/**
		 * @return true if menu item is enabled, false otherwise
		 */
		public boolean isEnabled() {
			return state.enabled;
		}

		/**
		 * @return true if this menu item has a sub menu
		 */
		public boolean hasSubMenu() {
			return state.getChildren().size() > 0;
		}

		/**
		 * @return true if this item is root item, false otherwise.
		 */
		public boolean isRootItem() {
			return parent == null;
		}

		/**
		 * Adds context menu item click listener only to this item. This
		 * listener will be invoked only when this item is clicked.
		 * 
		 * @param clickListener
		 */
		public void addItemClickListener(
				ContextMenu.ContextMenuItemClickListener clickListener) {
			this.clickListeners.add(clickListener);
		}

		/**
		 * Removes given click listener from this item. Removing listener
		 * affects only this context menu item.
		 * 
		 * @param clickListener
		 */
		public void removeItemClickListener(
				ContextMenu.ContextMenuItemClickListener clickListener) {
			this.clickListeners.remove(clickListener);
		}

		/**
		 * Add a new style to the menu item. This method is following the same
		 * semantics as {@link Component#addStyleName(String)}.
		 * 
		 * @param style
		 *            the new style to be added to the component
		 */
		public void addStyleName(String style) {
			if (style == null || style.isEmpty()) {
				return;
			}
			if (style.contains(" ")) {
				// Split space separated style names and add them one by one.
				StringTokenizer tokenizer = new StringTokenizer(style, " ");
				while (tokenizer.hasMoreTokens()) {
					addStyleName(tokenizer.nextToken());
				}
				return;
			}

			state.getStyles().add(style);
			markAsDirty();
		}

		/**
		 * Remove a style name from this menu item. This method is following the
		 * same semantics as {@link Component#removeStyleName(String)} .
		 * 
		 * @param style
		 *            the style name or style names to be removed
		 */
		public void removeStyleName(String style) {
			if (state.getStyles().isEmpty()) {
				return;
			}

			StringTokenizer tokenizer = new StringTokenizer(style, " ");
			while (tokenizer.hasMoreTokens()) {
				state.getStyles().remove(tokenizer.nextToken());
			}
		}

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}

			if (other instanceof ContextMenuItem) {
				return state.id.equals(((ContextMenuItem) other).state.id);
			}

			return false;
		}

		@Override
		public int hashCode() {
			return state.id.hashCode();
		}

		/**
		 * Changes the caption of the menu item
		 * 
		 * @param newCaption
		 */
		public void setCaption(String newCaption) {
			state.caption = newCaption;
			markAsDirty();
		}
	}

	/**
	 * ContextMenuItemClickListener is listener for context menu items wanting
	 * to notify listeners about item click
	 */
	public interface ContextMenuItemClickListener extends EventListener {

		public static final Method ITEM_CLICK_METHOD = ReflectTools.findMethod(
				ContextMenuItemClickListener.class, "contextMenuItemClicked",
				ContextMenuItemClickEvent.class);

		/**
		 * Called by the context menu item when it's clicked
		 * 
		 * @param event
		 *            containing the information of which item was clicked
		 */
		public void contextMenuItemClicked(ContextMenuItemClickEvent event);
	}

	/**
	 * ContextMenuItemClickEvent is an event produced by the context menu item
	 * when it is clicked. Event contains method for retrieving the clicked item
	 * and menu from which the click event originated.
	 */
	public static class ContextMenuItemClickEvent extends EventObject {
		private static final long serialVersionUID = -3301204853129409248L;

		public ContextMenuItemClickEvent(Object component) {
			super(component);
		}
	}

	/**
	 * ContextMenuClosedListener is used to listen for the event that the
	 * context menu is closed, either when a item is clicked or when the popup
	 * is canceled.
	 */
	public interface ContextMenuClosedListener extends EventListener {
		public static final Method MENU_CLOSED = ReflectTools.findMethod(
				ContextMenuClosedListener.class, "onContextMenuClosed",
				ContextMenuClosedEvent.class);

		/**
		 * Called when the context menu is closed
		 * 
		 * @param event
		 */
		public void onContextMenuClosed(ContextMenuClosedEvent event);
	}

	/**
	 * ContextMenuClosedEvent is an event fired by the context menu when it's
	 * closed.
	 */
	public static class ContextMenuClosedEvent extends EventObject {
		private static final long serialVersionUID = -5705205542849351984L;

		private final ContextMenu contextMenu;

		public ContextMenuClosedEvent(ContextMenu contextMenu) {
			super(contextMenu);
			this.contextMenu = contextMenu;
		}

		public ContextMenu getContextMenu() {
			return contextMenu;
		}
	}

	/**
	 * ContextMenuOpenedListener is used to modify the content of context menu
	 * based on what was clicked. For example TableListener can be used to
	 * modify context menu based on certain table component clicks.
	 * 
	 * @author Peter Lehto / Vaadin Ltd
	 * 
	 */
	public interface ContextMenuOpenedListener extends EventListener {

		/**
		 * ComponentListener is used when context menu is extending a component
		 * and works in mode where auto opening is disabled. For example if
		 * ContextMenu is assigned to a Layout and layout is right clicked when
		 * auto open feature is disabled, the open listener would be called
		 * instead of menu opening automatically. Example usage is for example
		 * as follows:
		 * 
		 * event.getContextMenu().open(event.getRequestSourceComponent());
		 * 
		 * @author Peter Lehto / Vaadin
		 */
		public interface ComponentListener extends ContextMenuOpenedListener {

			public static final Method MENU_OPENED_FROM_COMPONENT = ReflectTools
					.findMethod(
							ContextMenuOpenedListener.ComponentListener.class,
							"onContextMenuOpenFromComponent",
							ContextMenuOpenedOnComponentEvent.class);

			/**
			 * Called by the context menu when it's opened by clicking on
			 * component.
			 * 
			 * @param event
			 */
			public void onContextMenuOpenFromComponent(
					ContextMenuOpenedOnComponentEvent event);
		}

		/**
		 * ContextMenuOpenedListener.TableListener sub interface for table
		 * related context menus
		 * 
		 * @author Peter Lehto / Vaadin Ltd
		 */
		public interface TableListener extends ContextMenuOpenedListener {

			public static final Method MENU_OPENED_FROM_TABLE_ROW_METHOD = ReflectTools
					.findMethod(ContextMenuOpenedListener.TableListener.class,
							"onContextMenuOpenFromRow",
							ContextMenuOpenedOnTableRowEvent.class);

			public static final Method MENU_OPENED_FROM_TABLE_HEADER_METHOD = ReflectTools
					.findMethod(ContextMenuOpenedListener.TableListener.class,
							"onContextMenuOpenFromHeader",
							ContextMenuOpenedOnTableHeaderEvent.class);

			public static final Method MENU_OPENED_FROM_TABLE_FOOTER_METHOD = ReflectTools
					.findMethod(ContextMenuOpenedListener.TableListener.class,
							"onContextMenuOpenFromFooter",
							ContextMenuOpenedOnTableFooterEvent.class);

			/**
			 * Called by the context menu when it's opened by clicking table
			 * component's row
			 * 
			 * @param event
			 */
			public void onContextMenuOpenFromRow(
					ContextMenuOpenedOnTableRowEvent event);

			/**
			 * Called by the context menu when it's opened by clicking table
			 * component's header
			 * 
			 * @param event
			 */
			public void onContextMenuOpenFromHeader(
					ContextMenuOpenedOnTableHeaderEvent event);

			/**
			 * Called by the context menu when it's opened by clicking table
			 * component's footer
			 * 
			 * @param event
			 */
			public void onContextMenuOpenFromFooter(
					ContextMenuOpenedOnTableFooterEvent event);
		}

		public interface TreeListener extends ContextMenuOpenedListener {
			public static final Method MENU_OPENED_FROM_TREE_ITEM_METHOD = ReflectTools
					.findMethod(ContextMenuOpenedListener.TreeListener.class,
							"onContextMenuOpenFromTreeItem",
							ContextMenuOpenedOnTreeItemEvent.class);

			/**
			 * Called by the context menu when it's opened by clicking item on a
			 * tree.
			 * 
			 * @param event
			 */
			public void onContextMenuOpenFromTreeItem(
					ContextMenuOpenedOnTreeItemEvent event);
		}

	}

	/**
	 * ContextMenuOpenedOnTreeItemEvent is an event fired by the context menu
	 * when it's opened by clicking on tree item.
	 */
	public static class ContextMenuOpenedOnTreeItemEvent extends EventObject {
		private static final long serialVersionUID = -7705205542849351984L;

		private final Object itemId;
		private final ContextMenu contextMenu;

		public ContextMenuOpenedOnTreeItemEvent(ContextMenu contextMenu,
				Tree tree, Object itemId) {
			super(tree);

			this.contextMenu = contextMenu;
			this.itemId = itemId;
		}

		public ContextMenu getContextMenu() {
			return contextMenu;
		}

		public Object getItemId() {
			return itemId;
		}
	}

	/**
	 * ContextMenuOpenedOnTableHeaderEvent is an event fired by the context menu
	 * when it's opened by clicking on table header row.
	 */
	public static class ContextMenuOpenedOnTableHeaderEvent extends EventObject {
		private static final long serialVersionUID = -1220618848356241248L;

		private final Object propertyId;

		private final ContextMenu contextMenu;

		public ContextMenuOpenedOnTableHeaderEvent(ContextMenu contextMenu,
				Table source, Object propertyId) {
			super(source);

			this.contextMenu = contextMenu;
			this.propertyId = propertyId;
		}

		public ContextMenu getContextMenu() {
			return contextMenu;
		}

		public Object getPropertyId() {
			return propertyId;
		}
	}

	/**
	 * ContextMenuOpenedOnTableFooterEvent is an event that is fired by the
	 * context menu when it's opened by clicking on table footer
	 */
	public static class ContextMenuOpenedOnTableFooterEvent extends EventObject {
		private static final long serialVersionUID = 1999781663913723438L;

		private final Object propertyId;

		private final ContextMenu contextMenu;

		public ContextMenuOpenedOnTableFooterEvent(ContextMenu contextMenu,
				Table source, Object propertyId) {
			super(source);

			this.contextMenu = contextMenu;
			this.propertyId = propertyId;
		}

		public ContextMenu getContextMenu() {
			return contextMenu;
		}

		public Object getPropertyId() {
			return propertyId;
		}
	}

	/**
	 * ContextMenuOpenedOnTableRowEvent is an event that is fired when context
	 * menu is opened by clicking on table row.
	 */
	public static class ContextMenuOpenedOnTableRowEvent extends EventObject {
		private static final long serialVersionUID = -470218301318358912L;

		private final ContextMenu contextMenu;
		private final Object propertyId;
		private final Object itemId;

		public ContextMenuOpenedOnTableRowEvent(ContextMenu contextMenu,
				Table table, Object itemId, Object propertyId) {
			super(table);

			this.contextMenu = contextMenu;
			this.itemId = itemId;
			this.propertyId = propertyId;
		}

		public ContextMenu getContextMenu() {
			return contextMenu;
		}

		public Object getItemId() {
			return itemId;
		}

		public Object getPropertyId() {
			return propertyId;
		}
	}

	/**
	 * ContextMenuOpenedOnComponentEvent is an event fired by the context menu
	 * when it's opened from a component
	 * 
	 */
	public static class ContextMenuOpenedOnComponentEvent extends EventObject {
		private static final long serialVersionUID = 947108059398706966L;

		private final ContextMenu contextMenu;

		private final int x;
		private final int y;

		public ContextMenuOpenedOnComponentEvent(ContextMenu contextMenu,
				int x, int y, Component component) {
			super(component);

			this.contextMenu = contextMenu;
			this.x = x;
			this.y = y;
		}

		/**
		 * @return ContextMenu that was opened.
		 */
		public ContextMenu getContextMenu() {
			return contextMenu;
		}

		/**
		 * @return Component which initiated the context menu open request.
		 */
		public Component getRequestSourceComponent() {
			return (Component) getSource();
		}

		/**
		 * @return x-coordinate of open position.
		 */
		public int getX() {
			return x;
		}

		/**
		 * @return y-coordinate of open position.
		 */
		public int getY() {
			return y;
		}
	}

}
