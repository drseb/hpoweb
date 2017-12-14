package hpoweb.data.dataprovider.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.semanticweb.owlapi.model.OWLClass;

import com.google.common.collect.ImmutableSet;

import de.charite.phenowl.annotations.OwlAnnotatedDiseaseEntry;
import hpo.ItemId;
import hpoweb.data.dataprovider.IHpClassDataProvider;
import hpoweb.data.entities.DiseaseGene;
import hpoweb.uicontent.graph.GraphtestUI;
import hpoweb.uicontent.table.DiseaseGeneTableEntry;
import hpoweb.uicontent.table.GeneDiseaseTableEntry;

public class FakeHpClassDataProvider implements IHpClassDataProvider {

	private GraphtestUI graphUi;

	public FakeHpClassDataProvider() {

		this.graphUi = new GraphtestUI();
	}

	@Override
	public String getId() {
		return "HP:" + RandomStringUtils.randomNumeric(7);
	}

	@Override
	public String getLabel() {
		return RandomStringUtils.randomAlphabetic(20).toUpperCase() + " " + RandomStringUtils.randomAlphabetic(15).toUpperCase();
	}

	@Override
	public String getTypeOfEntityString() {
		return "fake HP class";
	}

	@Override
	public String getPrimaryId() {
		return "HP:" + RandomStringUtils.randomNumeric(7);
	}

	@Override
	public Collection<String> getAlternativeIds() {
		return ImmutableSet.of("HP:" + RandomStringUtils.randomNumeric(7), "HP:" + RandomStringUtils.randomNumeric(7),
				"HP:" + RandomStringUtils.randomNumeric(7), "HP:" + RandomStringUtils.randomNumeric(7), "HP:" + RandomStringUtils.randomNumeric(7));
	}

	@Override
	public GraphtestUI getGraphtestUi() {
		return this.graphUi;
	}

	@Override
	public String getIRI() {
		return "http://purl.obolibrary.org/obo/" + "HP_" + RandomStringUtils.randomNumeric(7);
	}

	@Override
	public Collection<String> getSynonyms() {
		return ImmutableSet.of(RandomStringUtils.randomAlphabetic(10).toUpperCase(), RandomStringUtils.randomAlphabetic(7).toUpperCase(),
				RandomStringUtils.randomAlphabetic(9).toUpperCase(), RandomStringUtils.randomAlphabetic(15).toUpperCase(),
				"ghfsadgfibba gfisbafkba hfjksbfkjsabf hfjksdhfsaf hflhsa fhsdahfa hfldshflsad hflsahfkldsaf");
	}

	@Override
	public String getTextdef() {
		return RandomStringUtils.randomAlphabetic(10).toUpperCase() + " " + RandomStringUtils.randomAlphabetic(7).toUpperCase() + " "
				+ RandomStringUtils.randomAlphabetic(9).toUpperCase() + " " + RandomStringUtils.randomAlphabetic(15).toUpperCase() + " "
				+ RandomStringUtils.randomAlphabetic(15).toUpperCase() + " " + RandomStringUtils.randomAlphabetic(15).toUpperCase();
	}

	@Override
	public String getLogicalDef() {
		return RandomStringUtils.randomAlphabetic(10).toUpperCase() + " " + RandomStringUtils.randomAlphabetic(7).toUpperCase() + " "
				+ RandomStringUtils.randomAlphabetic(9).toUpperCase() + "<br>" + RandomStringUtils.randomAlphabetic(5).toUpperCase() + " "
				+ RandomStringUtils.randomAlphabetic(15).toUpperCase() + " " + RandomStringUtils.randomAlphabetic(15).toUpperCase();
	}

	@Override
	public Collection<OWLClass> getSuperClasses() {
		return null;
	}

	@Override
	public Collection<OWLClass> getSubClasses() {
		return null;
	}

	@Override
	public List<DiseaseGeneTableEntry> getDiseaseGeneTableContent() {

		ArrayList<DiseaseGeneTableEntry> tableEntries = new ArrayList<DiseaseGeneTableEntry>();
		for (int i = 0; i < 250; i++) {
			ArrayList<DiseaseGene> associatedGenes = new ArrayList<DiseaseGene>();
			if (Math.random() < 0.3) {
				associatedGenes.add(getRandomDiseaseGene());
			}
			else if (Math.random() < 0.6) {
				associatedGenes.add(getRandomDiseaseGene());
				associatedGenes.add(getRandomDiseaseGene());
			}
			String diseaseId = RandomStringUtils.randomAlphabetic(5);
			String diseaseName = RandomStringUtils.randomAlphabetic(4) + " " + RandomStringUtils.randomAlphabetic(8);
			DiseaseGeneTableEntry entry = new DiseaseGeneTableEntry(diseaseId, diseaseName, associatedGenes);
			tableEntries.add(entry);
		}
		return tableEntries;
	}

	private DiseaseGene getRandomDiseaseGene() {
		DiseaseGene g = new DiseaseGene(RandomStringUtils.randomNumeric(5), RandomStringUtils.randomAlphanumeric(4).toUpperCase());
		return g;
	}

	@Override
	public List<GeneDiseaseTableEntry> getGeneDiseaseTableContent() {
		ArrayList<GeneDiseaseTableEntry> tableEntries = new ArrayList<GeneDiseaseTableEntry>();
		for (int i = 0; i < 250; i++) {
			ArrayList<OwlAnnotatedDiseaseEntry> associatedDiseases = new ArrayList<OwlAnnotatedDiseaseEntry>();
			if (Math.random() < 0.3) {
				associatedDiseases.add(getRandomDisease());
			}
			else if (Math.random() < 0.6) {
				associatedDiseases.add(getRandomDisease());
				associatedDiseases.add(getRandomDisease());
			}
			String geneId = RandomStringUtils.randomNumeric(5);
			String geneSymbol = RandomStringUtils.randomAlphabetic(4).toUpperCase();
			GeneDiseaseTableEntry entry = new GeneDiseaseTableEntry(geneId, geneSymbol, associatedDiseases);
			tableEntries.add(entry);
		}
		return tableEntries;
	}

	private OwlAnnotatedDiseaseEntry getRandomDisease() {
		ItemId id = new ItemId("OMIM", RandomStringUtils.randomNumeric(6));
		OwlAnnotatedDiseaseEntry entry = new OwlAnnotatedDiseaseEntry(id, null, null);
		entry.setName(RandomStringUtils.randomAlphabetic(8) + " " + RandomStringUtils.randomAlphabetic(4) + " syndrome");
		return entry;
	}
}
