package hpoweb.uicontent.table;

import org.jsoup.Jsoup;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

public class TableLabel extends Label {

	public TableLabel(String string, ContentMode html) {
		super(string, html);
	}

	@Override
	public String toString() {
		return Jsoup.parse(getValue()).text();
	}
}
