package hpoweb.util;

import org.vaadin.viritin.fields.MValueChangeEvent;
import org.vaadin.viritin.fields.MValueChangeListener;

import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.Page;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import hpoweb.HpowebUI;
import hpoweb.data.entities.SearchableEntity;
import hpoweb.data.entities.SearchableEntity.SearchableEntityType;

public class UpdatePageClickListener
		implements ClickListener, ItemClickListener, MValueChangeListener<SearchableEntity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3327642630118820000L;
	private String id;
	private String requestType;

	public UpdatePageClickListener(String id, String requestType) {
		this.id = id;
		this.requestType = requestType;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		// Page page = HpowebUI.get().getPage();
		// page.setUriFragment(requestType + "=" + id);
		updateUi(requestType, id);
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

		// Page page = HpowebUI.get().getPage();
		// page.setUriFragment(requestType + "=" + id);
		updateUi(requestType, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.vaadin.viritin.fields.MValueChangeListener#valueChange(org.vaadin.viritin
	 * .fields.MValueChangeEvent)
	 */
	@Override
	public void valueChange(MValueChangeEvent<SearchableEntity> event) {
		String searchParameter = null;
		if (event.getValue().getType().equals(SearchableEntityType.HPO)) {
			searchParameter = CONSTANTS.hpRequestId;
		}
		else if (event.getValue().getType().equals(SearchableEntityType.Gene)) {
			searchParameter = CONSTANTS.geneRequestId;
		}
		else if (event.getValue().getType().equals(SearchableEntityType.Disease)) {
			searchParameter = CONSTANTS.diseaseRequestId;
		}

		String searchValue = event.getValue().getSearchValue();

		// Page.getCurrent().setLocation(CONSTANTS.rootLocation + "?" + searchParameter
		// + "=" + searchValue);
		updateUi(searchParameter, searchValue);

	}

	/**
	 * @param searchParameter
	 * @param searchValue
	 */
	private void updateUi(String searchParameter, String searchValue) {

		HpowebUI.get().accessSynchronously(new Runnable() {

			@Override
			public void run() {
				Page page = HpowebUI.get().getPage();
				page.setUriFragment(searchParameter + "=" + searchValue);
			}
		});

	}

}
