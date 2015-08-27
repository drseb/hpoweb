package hpoweb.uicontent.table;

import hpoweb.data.entities.DiseaseGene;

import java.util.ArrayList;

public class DiseaseGeneTableEntry {

	private String diseaseId;
	private String diseaseName;
	private ArrayList<DiseaseGene> associatedGenes;
	private String description;

	public DiseaseGeneTableEntry(String diseaseId, String diseaseName, ArrayList<DiseaseGene> associatedGenes) {
		super();
		this.diseaseId = diseaseId;
		this.diseaseName = diseaseName;
		this.associatedGenes = associatedGenes;
	}

	public String getDiseaseId() {
		return diseaseId;
	}

	public String getDiseaseName() {
		return diseaseName;
	}

	public ArrayList<DiseaseGene> getAssociatedGenes() {
		return associatedGenes;
	}

}
