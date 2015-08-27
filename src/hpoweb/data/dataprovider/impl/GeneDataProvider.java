package hpoweb.data.dataprovider.impl;

import hpoweb.data.HpData;
import hpoweb.data.dataprovider.IGeneDataProvider;
import hpoweb.uicontent.table.HpoClassTableEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.semanticweb.owlapi.model.OWLClass;

import com.google.common.collect.HashMultimap;

import de.charite.phenowl.annotations.AnnotationUtils;
import de.charite.phenowl.annotations.DiseaseEntry;
import de.charite.phenowl.annotations.DiseaseId;
import de.charite.phenowl.hpowl.util.OboUtil;

public class GeneDataProvider implements IGeneDataProvider {

	private Integer geneId;
	private String symbol;
	private HashMultimap<OWLClass, DiseaseEntry> hpClass2annotatedDiseases;
	private HpData hpData;
	private ArrayList<DiseaseEntry> associatedDiseases;

	public GeneDataProvider(Integer geneId, HpData hpData) {
		this.hpData = hpData;
		AnnotationUtils util = hpData.getAnnotationUtils();
		this.geneId = geneId;
		this.symbol = util.getDiseaseGeneMapper().entrez2symbol.get(geneId);

		hpClass2annotatedDiseases = HashMultimap.create();
		associatedDiseases = new ArrayList<DiseaseEntry>();
		if (util.getDiseaseGeneMapper().entrezId2diseaseIds.containsKey(geneId)) {
			for (DiseaseId diseaseId : util.getDiseaseGeneMapper().entrezId2diseaseIds.get(geneId)) {
				if (!util.getDiseaseId2entry().containsKey(diseaseId))
					continue;
				DiseaseEntry entry = util.getDiseaseId2entry().get(diseaseId);
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
			for (DiseaseEntry e : hpClass2annotatedDiseases.get(cls)) {
				description += " - " + e.getName() + "<br>";
			}
			HpoClassTableEntry entry = new HpoClassTableEntry(hpoId, hpoLabel, description);
			tableentries.add(entry);
		}

		return tableentries;

	}

	@Override
	public Collection<DiseaseEntry> getAssociatedDiseases() {
		return associatedDiseases;
	}
}
