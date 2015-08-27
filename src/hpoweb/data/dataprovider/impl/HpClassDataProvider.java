package hpoweb.data.dataprovider.impl;

import hpoweb.data.HpData;
import hpoweb.data.dataprovider.IHpClassDataProvider;
import hpoweb.data.entities.DiseaseGene;
import hpoweb.uicontent.graph.GraphtestUI;
import hpoweb.uicontent.table.DiseaseGeneTableEntry;
import hpoweb.uicontent.table.GeneDiseaseTableEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.semanticweb.owlapi.model.OWLClass;

import com.google.common.collect.ImmutableSet;

import de.charite.phenowl.annotations.AnnotationUtils;
import de.charite.phenowl.annotations.DiseaseEntry;
import de.charite.phenowl.annotations.DiseaseId;
import de.charite.phenowl.hpowl.util.ExtendedOwlOntology;
import de.charite.phenowl.hpowl.util.OboUtil;

public class HpClassDataProvider implements IHpClassDataProvider {

	private OWLClass hpClass;
	private String label;
	private ImmutableSet<String> alternativeIds;
	private String primaryId;
	private String iriString;
	private ImmutableSet<OWLClass> superClasses;
	private ImmutableSet<OWLClass> subClasses;
	private GraphtestUI graphUi;
	private ImmutableSet<String> synonyms;
	private String textdef;
	private String logicaldef;
	private HashSet<DiseaseId> associatedDiseaseIds;
	private AnnotationUtils utils;
	private HashSet<Integer> associatedGeneIds;

	public HpClassDataProvider(OWLClass hpClass, HpData hpData) {
		this.hpClass = hpClass;

		ExtendedOwlOntology ontology = hpData.getExtOwlOntology();
		this.label = ontology.getLabel(hpClass.getIRI());

		this.alternativeIds = hpData.getExtOwlOntology().getAlternativeIdsForClass(hpClass);
		this.primaryId = OboUtil.IRI2ID(hpClass.getIRI());
		this.iriString = hpClass.getIRI().toString();

		this.superClasses = hpData.getExtOwlOntology().getParents(hpClass);
		this.subClasses = hpData.getExtOwlOntology().getChildren(hpClass);
		this.synonyms = hpData.getExtOwlOntology().getSynonymsForClass(hpClass);
		this.textdef = hpData.getExtOwlOntology().getTextdefForClass(hpClass);
		this.logicaldef = hpData.getExtOwlOntology().getLogicalDefForClassForVaadin(hpClass);

		this.graphUi = new GraphtestUI(hpClass, hpData);
		this.utils = hpData.getAnnotationUtils();
		this.associatedDiseaseIds = utils.getHpoClass2diseases().get(hpClass);
		this.associatedGeneIds = utils.getHpoClass2entrezIds().get(hpClass);

	}

	/**
	 * @return the superClasses
	 */
	@Override
	public ImmutableSet<OWLClass> getSuperClasses() {
		return superClasses;
	}

	/**
	 * @return the subClasses
	 */
	@Override
	public ImmutableSet<OWLClass> getSubClasses() {
		return subClasses;
	}

	@Override
	public String getId() {
		return primaryId;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getTypeOfEntityString() {
		return "HPO class";
	}

	public ImmutableSet<String> getAlternativeIds() {
		return alternativeIds;
	}

	public OWLClass getHpClass() {
		return hpClass;
	}

	public String getIriString() {
		return iriString;
	}

	public String getPrimaryId() {
		return primaryId;
	}

	@Override
	public GraphtestUI getGraphtestUi() {
		return this.graphUi;
	}

	@Override
	public String getIRI() {
		return hpClass.getIRI().toString();
	}

	@Override
	public Collection<String> getSynonyms() {
		return synonyms;
	}

	@Override
	public String getTextdef() {
		return textdef;
	}

	@Override
	public String getLogicalDef() {
		return logicaldef;
	}

	@Override
	public List<DiseaseGeneTableEntry> getDiseaseGeneTableContent() {
		ArrayList<DiseaseGeneTableEntry> tableentries = new ArrayList<DiseaseGeneTableEntry>();

		if (this.associatedDiseaseIds == null)
			return tableentries;

		for (DiseaseId dID : this.associatedDiseaseIds) {
			DiseaseEntry entry = utils.getDiseaseId2entry().get(dID);
			String diseaseId = dID.toString();
			String diseaseName = entry.getName();
			ArrayList<DiseaseGene> associatedGenes = new ArrayList<DiseaseGene>();
			HashSet<Integer> geneIds = utils.getDiseaseGeneMapper().diseaseId2entrezIds.get(dID);
			if (geneIds != null)
				for (int geneId : geneIds) {
					String symbol = utils.getDiseaseGeneMapper().entrez2symbol.get(geneId);
					DiseaseGene g = new DiseaseGene(geneId + "", symbol);
					associatedGenes.add(g);
				}
			DiseaseGeneTableEntry tableentry = new DiseaseGeneTableEntry(diseaseId, diseaseName, associatedGenes);
			tableentries.add(tableentry);
		}
		return tableentries;
	}

	@Override
	public List<GeneDiseaseTableEntry> getGeneDiseaseTableContent() {
		ArrayList<GeneDiseaseTableEntry> tableentries = new ArrayList<GeneDiseaseTableEntry>();

		if (this.associatedGeneIds == null)
			return tableentries;

		for (int geneId : this.associatedGeneIds) {
			String geneSymbol = utils.getDiseaseGeneMapper().entrez2symbol.get(geneId);

			ArrayList<DiseaseEntry> associatedDiseases = new ArrayList<DiseaseEntry>();
			HashSet<DiseaseId> diseaseIds = utils.getDiseaseGeneMapper().entrezId2diseaseIds.get(geneId);

			if (diseaseIds != null)
				for (DiseaseId diseaseId : diseaseIds) {
					if (utils.getDiseaseId2entry().containsKey(diseaseId)) {
						DiseaseEntry diseaseentry = utils.getDiseaseId2entry().get(diseaseId);
						associatedDiseases.add(diseaseentry);
					}
				}
			GeneDiseaseTableEntry tableentry = new GeneDiseaseTableEntry(geneId + "", geneSymbol, associatedDiseases);
			tableentries.add(tableentry);
		}
		return tableentries;
	}
}
