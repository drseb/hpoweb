package hpoweb.uicontent.tabs.gene;

import hpoweb.data.dataprovider.IGeneDataProvider;
import hpoweb.uicontent.table.HpoClassTableEntry;
import hpoweb.uicontent.table.TableLabel;
import hpoweb.util.CONSTANTS;
import hpoweb.util.TableUtils;

import java.util.Collection;
import java.util.List;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import de.charite.phenowl.annotations.DiseaseEntry;

public class GeneTabFactory {

	private TableUtils tableUtils;

	public GeneTabFactory(TableUtils tableUtils) {
		this.tableUtils = tableUtils;
	}

	public void addGeneInfoTabs(TabSheet sheet, IGeneDataProvider dataProvider) {

		/*
		 * Associated terms
		 */
		VerticalLayout vl_hpoclasses = getAssociatedHpoClasses(dataProvider);
		sheet.addTab(vl_hpoclasses, "HPO classes");

		/*
		 * Associated diseases
		 */
		VerticalLayout vl_diseases = getAssociatedDiseases(dataProvider);
		sheet.addTab(vl_diseases, "Associated diseases");
	}

	private VerticalLayout getAssociatedDiseases(IGeneDataProvider dataProvider) {
		VerticalLayout vl_genes = new VerticalLayout();

		Collection<DiseaseEntry> diseases = dataProvider.getAssociatedDiseases();
		if (diseases.size() < 1) {
			Label l = new Label("No diseases associated with this gene.");
			l.addStyleName("tab-content-content");
			vl_genes.addComponent(l);
		} else {
			for (DiseaseEntry disease : diseases) {
				Label l = new Label(disease.getName() + " (<a href='" + CONSTANTS.rootLocation + "?" + CONSTANTS.diseaseRequestId + "="
						+ disease.getDiseaseIdAsString() + "'>" + disease.getDiseaseIdAsString() + "</a>)", ContentMode.HTML);
				l.addStyleName("tab-content-content");
				vl_genes.addComponent(l);
			}
		}

		vl_genes.addStyleName("tab-content-vl");
		return vl_genes;
	}

	private VerticalLayout getAssociatedHpoClasses(IGeneDataProvider dataProvider) {
		VerticalLayout tableVL = new VerticalLayout();
		tableVL.setSizeFull();

		Table table = new Table();
		table.addContainerProperty("HPO id", TableLabel.class, null);
		table.addContainerProperty("HPO label", TableLabel.class, null);
		table.setSizeFull();

		List<HpoClassTableEntry> tableContent = dataProvider.getAssociatedHpoClasses();

		int id = 0;
		for (HpoClassTableEntry entry : tableContent) {
			TableLabel hpoid = new TableLabel("<a href='" + CONSTANTS.rootLocation + "?" + CONSTANTS.hpRequestId + "=" + entry.getHpoId() + "'>"
					+ entry.getHpoId() + "</a>", ContentMode.HTML);
			TableLabel hpolabel = new TableLabel(entry.getHpoLabel(), ContentMode.HTML);
			hpoid.setDescription(entry.getDescription());
			hpolabel.setDescription(entry.getDescription());

			Integer itemId = new Integer(id++);
			table.addItem(new Object[] { hpoid, hpolabel }, itemId);
		}

		tableVL.addComponent(table);

		String geneId = dataProvider.getId();
		String header = "Export for " + geneId;
		String fileName = "hpoterms_for_" + geneId;
		tableUtils.addDownloadButtons(tableVL, table, fileName, header);

		tableVL.addStyleName("tab-content-vl");
		tableVL.setExpandRatio(table, 1f);
		return tableVL;
	}

}
