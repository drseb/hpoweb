package hpoweb.uicontent.table;

public class HpoClassTableEntry {

	private String hpoId;
	private String hpoLabel;
	private String description;

	public HpoClassTableEntry(String hpoId, String hpoLabel, String description) {
		super();
		this.hpoId = hpoId;
		this.hpoLabel = hpoLabel;
		this.description = description;
	}

	public String getHpoId() {
		return hpoId;
	}

	public String getHpoLabel() {
		return hpoLabel;
	}

	public String getDescription() {
		return description;
	}

}
