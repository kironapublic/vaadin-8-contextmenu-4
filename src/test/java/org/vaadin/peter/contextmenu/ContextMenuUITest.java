package org.vaadin.peter.contextmenu;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class ContextMenuUITest extends UI {

	@Override
	protected void init(VaadinRequest request) {
		setSizeFull();

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		
		setContent(layout);
		
		final ContextMenuBasic contextMenu = new ContextMenuBasic();
		layout.addComponent(contextMenu);
		
		contextMenu.addItem("Test item #1").addItem("Test Item #2");
		
		layout.addLayoutClickListener(new LayoutClickListener() {
			
			@Override
			public void layoutClick(LayoutClickEvent event) {
				contextMenu.openAt(event.getClientX(), event.getClientY());
			}
		});
		
	}

}
