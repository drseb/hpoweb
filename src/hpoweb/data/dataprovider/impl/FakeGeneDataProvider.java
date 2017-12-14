package hpoweb.data.dataprovider.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

import com.google.gwt.thirdparty.guava.common.collect.Lists;

import de.charite.phenowl.annotations.OwlAnnotatedDiseaseEntry;
import hpo.ItemId;
import hpoweb.data.dataprovider.IGeneDataProvider;
import hpoweb.uicontent.table.HpoClassTableEntry;

public class FakeGeneDataProvider implements IGeneDataProvider {

	private String id;
	private String symbol;

	public FakeGeneDataProvider() {
		this.id = RandomStringUtils.randomNumeric(5);
		this.symbol = RandomStringUtils.randomAlphabetic(4).toUpperCase();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return symbol + " (" + id + ")";
	}

	@Override
	public String getTypeOfEntityString() {
		return "fake human gene";
	}

	@Override
	public List<HpoClassTableEntry> getAssociatedHpoClasses() {
		ArrayList<HpoClassTableEntry> tableEntries = new ArrayList<HpoClassTableEntry>();
		for (int i = 0; i < 50; i++) {

			String hpoId = "HP:" + RandomStringUtils.randomNumeric(7);
			String hpoLab = "Abnormality of " + RandomStringUtils.randomAlphabetic(4) + " " + RandomStringUtils.randomAlphabetic(8);
			HpoClassTableEntry entry = new HpoClassTableEntry(hpoId, hpoLab, "fake description here");
			tableEntries.add(entry);
		}
		return tableEntries;
	}

	@Override
	public Collection<OwlAnnotatedDiseaseEntry> getAssociatedDiseases() {

		ItemId id = new ItemId("OMIM", RandomStringUtils.randomNumeric(6));
		OwlAnnotatedDiseaseEntry entry = new OwlAnnotatedDiseaseEntry(id, null, null);
		entry.setName(RandomStringUtils.randomAlphabetic(8) + " " + RandomStringUtils.randomAlphabetic(4) + " syndrome");
		return Lists.newArrayList(entry);
	}

}
