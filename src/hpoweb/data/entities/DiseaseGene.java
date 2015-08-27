package hpoweb.data.entities;

public class DiseaseGene {

	private String geneId;
	private String geneSymbol;

	public DiseaseGene(String geneId, String geneSymbol) {
		super();
		this.geneId = geneId;
		this.geneSymbol = geneSymbol;
	}

	public String getGeneId() {
		return geneId;
	}

	public String getGeneSymbol() {
		return geneSymbol;
	}

}
