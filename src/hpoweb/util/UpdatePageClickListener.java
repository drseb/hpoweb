package hpoweb.util;

import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.Page;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import hpoweb.HpowebUI;

public class UpdatePageClickListener implements ClickListener, ItemClickListener {

	private String id;
	private String requestType;

	public UpdatePageClickListener(String id, String requestType) {
		this.id = id;
		this.requestType = requestType;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		Page page = HpowebUI.get().getPage();
		page.setUriFragment(requestType + "=" + id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vaadin.event.ItemClickEvent.ItemClickListener#itemClick(com.vaadin.event.
	 * ItemClickEvent)
	 */
	@Override
	public void itemClick(ItemClickEvent itemClickEvent) {
		Item item = itemClickEvent.getItem();
		String id = null;
		if (requestType.equals(CONSTANTS.diseaseRequestId)) {
			id = item.getItemProperty("Disease id").getValue().toString();
		}
		else if (requestType.equals(CONSTANTS.geneRequestId)) {
			id = item.getItemProperty("Gene").getValue().toString();
			id = id.replaceAll(".+\\(", "");
			id = id.replaceAll("\\).*", "");
		}
		else if (requestType.equals(CONSTANTS.hpRequestId)) {
			id = item.getItemProperty("HPO id").getValue().toString();
			id = id.replaceAll(":", "_");
		}

		Page page = HpowebUI.get().getPage();
		page.setUriFragment(requestType + "=" + id);
	}

}
