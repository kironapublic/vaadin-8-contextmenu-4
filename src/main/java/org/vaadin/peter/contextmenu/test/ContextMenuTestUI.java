package org.vaadin.peter.contextmenu.test;

import org.vaadin.peter.contextmenu.ContextMenu;
import org.vaadin.peter.contextmenu.ContextMenuBasic;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class ContextMenuTestUI extends UI {

	private VerticalLayout layout;
	
	private ContextMenu contextMenu;
	
	private LayoutClickListener clickListener = new LayoutClickListener() {
		
		@Override
		public void layoutClick(LayoutClickEvent event) {
			contextMenu.openAt(event.getClientX(), event.getClientY());			
		}
	};

	@Override
	protected void init(VaadinRequest request) {
		layout = new VerticalLayout();

		layout.setSizeFull();
		
		setContent(layout);
		
		ContextMenu contextMenu = new ContextMenuBasic();
		contextMenu.addItem("Test");
		contextMenu.addItem("Halp!");
		
		layout.addComponent(contextMenu);
		layout.addLayoutClickListener(clickListener);
		
		
		
	}

}
