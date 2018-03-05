package hpoweb.uicontent;

import java.io.Serializable;
import java.util.List;

import org.vaadin.viritin.LazyList;
import org.vaadin.viritin.fields.CaptionGenerator;
import org.vaadin.viritin.fields.LazyComboBox;

import hpoweb.data.HpData;
import hpoweb.data.dataprovider.LazySearchbarService;
import hpoweb.data.entities.SearchableEntity;
import hpoweb.util.UpdatePageClickListener;

public class SearchBarFactory implements Serializable {

	private static final long serialVersionUID = 7526472295622776147L;

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

		final LazyComboBox<SearchableEntity> cb = new LazyComboBox<SearchableEntity>(SearchableEntity.class,
				filterablePagingProvider, filterableCountProvider)
						.setCaptionGenerator(new CaptionGenerator<SearchableEntity>() {

							@Override
							public String getCaption(SearchableEntity option) {
								return option.toString();
							}
						});

		cb.setInputPrompt("Enter search terms ...");

		cb.addMValueChangeListener(new UpdatePageClickListener(null, null));

		return cb;

	}

}
