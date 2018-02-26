package hpoweb.util;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;

public class UpdatePageClickListener implements ClickListener {

	private String id;
	private String label;

	public UpdatePageClickListener(String id, String label) {
		this.id = id;
		this.label = label;

	}

	@Override
	public void buttonClick(ClickEvent event) {
		Notification.show(id + " " + label);
	}

}
