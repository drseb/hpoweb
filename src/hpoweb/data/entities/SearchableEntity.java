package hpoweb.data.entities;

import com.sun.istack.internal.NotNull;

public class SearchableEntity implements Comparable<SearchableEntity> {

	public enum SearchableEntityType {
		Disease, Gene, HPO
	}

	@NotNull
	private String label;
	private SearchableEntityType type;
	private String searchValue;

	public SearchableEntity(String searchAble, String searchPostValue, SearchableEntityType type) {
		this.label = searchAble;
		this.type = type;
		this.searchValue = searchPostValue;
	}

	public String getLabel() {
		return label;
	}

	public SearchableEntityType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "(" + type + ") " + label;
	}

	// TODO add hashcode + equals

	public String getSearchValue() {

		return searchValue;
	}

	@Override
	public int compareTo(SearchableEntity o) {
		return label.compareTo(o.label);
	}

}
