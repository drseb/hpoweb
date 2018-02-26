package hpoweb.data.dataprovider.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

import com.google.common.collect.ImmutableSet;

import hpoweb.data.HpData;
import hpoweb.data.dataprovider.IDiseaseDataProvider;
import hpoweb.data.entities.DiseaseGene;
import hpoweb.uicontent.table.HpoClassTableEntry;

public class FakeDiseaseDataProvider implements IDiseaseDataProvider {

	public FakeDiseaseDataProvider() {

	}

	@Override
	public String getId() {
		return "OMIM:" + RandomStringUtils.randomNumeric(6);
	}

	@Override
	public String getLabel() {
		return RandomStringUtils.randomAlphabetic(4) + " " + RandomStringUtils.randomAlphabetic(7) + " syndrome";
	}

	@Override
	public String getTypeOfEntityString() {
		return "fake disease entry";
	}

	@Override
	public Collection<String> getAlternativeNames() {
		double rand = Math.random();
		if (rand > 0.3) {
			return ImmutableSet.of(RandomStringUtils.randomAlphabetic(10).toUpperCase(),
					RandomStringUtils.randomAlphabetic(7).toUpperCase(),
					RandomStringUtils.randomAlphabetic(9).toUpperCase(),
					RandomStringUtils.randomAlphabetic(15).toUpperCase(),
					"ghfsadgfibba gfisbafkba hfjksbfkjsabf hfjksdhfsaf hflhsa fhsdahfa hfldshflsad hflsahfkldsaf");
		} else {
			return ImmutableSet.of();
		}
	}

	@Override
	public List<HpoClassTableEntry> getAnnotatedHpoClasses() {
		ArrayList<HpoClassTableEntry> tableEntries = new ArrayList<HpoClassTableEntry>();
		for (int i = 0; i < 50; i++) {

			String hpoId = "HP:" + RandomStringUtils.randomNumeric(7);
			String hpoLab = "Abnormality of " + RandomStringUtils.randomAlphabetic(4) + " "
					+ RandomStringUtils.randomAlphabetic(8);
			HpoClassTableEntry entry = new HpoClassTableEntry(hpoId, hpoLab, "fake description here");
			tableEntries.add(entry);
		}
		return tableEntries;
	}

	@Override
	public Collection<DiseaseGene> getAssociatedGenes() {
		double rand = Math.random();
		HashSet<DiseaseGene> genes = new HashSet<DiseaseGene>();
		if (rand > 0.3) {
			for (int i = 0; i < 2; i++) {
				String id = RandomStringUtils.randomNumeric(4);
				String sym = RandomStringUtils.randomAlphabetic(5).toUpperCase();
				DiseaseGene g = new DiseaseGene(id, sym);
				genes.add(g);
			}
			return genes;
		} else {
			return ImmutableSet.of();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hpoweb.data.dataprovider.IEntityDataProvider#getHpData()
	 */
	@Override
	public HpData getHpData() {
		// TODO Auto-generated method stub
		return null;
	}
}
