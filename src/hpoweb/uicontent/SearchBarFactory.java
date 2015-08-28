package hpoweb.uicontent;

import hpoweb.data.HpData;
import hpoweb.data.dataprovider.LazySearchbarService;
import hpoweb.data.entities.SearchableEntity;
import hpoweb.data.entities.SearchableEntity.SearchableEntityType;
import hpoweb.util.CONSTANTS;

import java.util.List;

import org.vaadin.viritin.LazyList;
import org.vaadin.viritin.fields.CaptionGenerator;
import org.vaadin.viritin.fields.LazyComboBox;
import org.vaadin.viritin.fields.MValueChangeEvent;
import org.vaadin.viritin.fields.MValueChangeListener;

import com.vaadin.server.Page;

public class SearchBarFactory {

	public LazyComboBox<SearchableEntity> getSearchBar(HpData hpData) {

		final LazySearchbarService service = new LazySearchbarService(hpData);

		final LazyComboBox.FilterablePagingProvider<SearchableEntity> filterablePagingProvider = new LazyComboBox.FilterablePagingProvider<SearchableEntity>() {

			@Override
			public List<SearchableEntity> findEntities(int firstRow, String filter) {
				return service.findMatches(filter, firstRow, LazyList.DEFAULT_PAGE_SIZE);
			}
		};

		final LazyComboBox.FilterableCountProvider filterableCountProvider = new LazyComboBox.FilterableCountProvider() {

			@Override
			public int size(String filter) {
				return service.countMatches(filter);
			}
		};

		final LazyComboBox<SearchableEntity> cb = new LazyComboBox<SearchableEntity>(SearchableEntity.class, filterablePagingProvider,
				filterableCountProvider).setCaptionGenerator(new CaptionGenerator<SearchableEntity>() {

			@Override
			public String getCaption(SearchableEntity option) {
				return option.toString();
			}
		});

		cb.setInputPrompt("Enter search terms ...");

		cb.addMValueChangeListener(new MValueChangeListener<SearchableEntity>() {

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

				Page.getCurrent().setLocation(CONSTANTS.rootLocation + "?" + searchParameter + "=" + searchValue);
			}
		});

		return cb;

	}

}
