package hpoweb.uicontent.table;

import java.util.ArrayList;

import de.charite.phenowl.annotations.DiseaseEntry;

public class GeneDiseaseTableEntry {

	private String geneId;
	private String geneSymbol;
	private ArrayList<DiseaseEntry> associatedDiseases;

	/**
	 * @return the geneId
	 */
	public String getGeneId() {
		return geneId;
	}

	/**
	 * @return the geneSymbol
	 */
	public String getGeneSymbol() {
		return geneSymbol;
	}

	/**
	 * @return the associatedDiseases
	 */
	public ArrayList<DiseaseEntry> getAssociatedDiseases() {
		return associatedDiseases;
	}

	public GeneDiseaseTableEntry(String geneId, String geneSymbol, ArrayList<DiseaseEntry> associatedDiseases) {
		super();
		this.geneId = geneId;
		this.geneSymbol = geneSymbol;
		this.associatedDiseases = associatedDiseases;
	}
}
