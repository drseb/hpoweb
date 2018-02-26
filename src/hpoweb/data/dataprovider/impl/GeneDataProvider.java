package hpoweb.data.dataprovider.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.semanticweb.owlapi.model.OWLClass;

import com.google.common.collect.HashMultimap;

import de.charite.phenowl.annotations.AnnotationUtils;
import de.charite.phenowl.annotations.OwlAnnotatedDiseaseEntry;
import de.charite.phenowl.hpowl.util.OboUtil;
import hpo.Item;
import hpo.ItemId;
import hpoweb.data.HpData;
import hpoweb.data.dataprovider.IGeneDataProvider;
import hpoweb.uicontent.table.HpoClassTableEntry;

public class GeneDataProvider implements IGeneDataProvider {

	private Integer geneId;
	private String symbol;
	private HashMultimap<OWLClass, OwlAnnotatedDiseaseEntry> hpClass2annotatedDiseases;
	private HpData hpData;
	private ArrayList<OwlAnnotatedDiseaseEntry> associatedDiseases;

	public GeneDataProvider(Integer geneId, HpData hpData) {
		this.hpData = hpData;
		AnnotationUtils util = hpData.getAnnotationUtils();
		this.geneId = geneId;
		this.symbol = util.getDiseaseGeneMapper().entrez2symbol.get(geneId);

		hpClass2annotatedDiseases = HashMultimap.create();
		associatedDiseases = new ArrayList<OwlAnnotatedDiseaseEntry>();
		if (util.getDiseaseGeneMapper().entrezId2diseaseIds.containsKey(geneId)) {
			for (ItemId diseaseId : util.getDiseaseGeneMapper().entrezId2diseaseIds.get(geneId)) {
				if (!util.getDiseaseId2entry().containsKey(diseaseId))
					continue;
				OwlAnnotatedDiseaseEntry entry = util.getDiseaseId2entry().get(diseaseId);
				associatedDiseases.add(entry);
				ArrayList<OWLClass> owlclasses = entry.getAllAssociatedOWLClasses();
				for (OWLClass cls : owlclasses) {
					hpClass2annotatedDiseases.put(cls, entry);
				}
			}
		}

	}

	@Override
	public String getId() {
		return geneId + "";
	}

	@Override
	public String getLabel() {
		return symbol + " (" + geneId + ")";
	}

	@Override
	public String getTypeOfEntityString() {
		return "human gene";
	}

	@Override
	public List<HpoClassTableEntry> getAssociatedHpoClasses() {

		ArrayList<HpoClassTableEntry> tableentries = new ArrayList<HpoClassTableEntry>();

		for (OWLClass cls : hpClass2annotatedDiseases.keySet()) {

			String hpoId = OboUtil.IRI2ID(cls.getIRI());
			String hpoLabel = hpData.getExtOwlOntology().getLabel(cls.getIRI());
			String description = "This annotation is supported by:<br>";
			for (Item e : hpClass2annotatedDiseases.get(cls)) {
				description += " - " + e.getName() + "<br>";
			}
			HpoClassTableEntry entry = new HpoClassTableEntry(hpoId, hpoLabel, description);
			tableentries.add(entry);
		}

		return tableentries;

	}

	@Override
	public Collection<OwlAnnotatedDiseaseEntry> getAssociatedDiseases() {
		return associatedDiseases;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hpoweb.data.dataprovider.IEntityDataProvider#getHpData()
	 */
	@Override
	public HpData getHpData() {
		return hpData;
	}
}
