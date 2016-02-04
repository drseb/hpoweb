package hpoweb.uicontent.tabs.disease;

import java.util.Collection;
import java.util.List;

import com.sebworks.vaadstrap.Col;
import com.sebworks.vaadstrap.ColMod;
import com.sebworks.vaadstrap.Container;
import com.sebworks.vaadstrap.Row;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import hpoweb.data.dataprovider.IDiseaseDataProvider;
import hpoweb.data.entities.DiseaseGene;
import hpoweb.uicontent.table.HpoClassTableEntry;
import hpoweb.uicontent.table.TableLabel;
import hpoweb.util.CONSTANTS;
import hpoweb.util.TableUtils;

public class DiseaseTabFactory {

	// private HpData hpData;
	private TableUtils tableUtils;

	public DiseaseTabFactory(TableUtils utils) {
		this.tableUtils = utils;
	}

	public void addDiseaseInfoTabs(TabSheet sheet, IDiseaseDataProvider dataProvider) {

		/*
		 * Alt names / synonyms
		 */
		VerticalLayout vl_altNames = getAltNamesTab(dataProvider);
		sheet.addTab(vl_altNames, "Synonyms");

		/*
		 * Associated terms
		 */
		VerticalLayout vl_terms = getAnnotatedHpoClassesTab(dataProvider);
		sheet.addTab(vl_terms, "HPO classes");

		/*
		 * Associated genes
		 */
		VerticalLayout vl_genes = getAssociatedGenesTab(dataProvider);
		sheet.addTab(vl_genes, "Genes");
	}

	private VerticalLayout getAssociatedGenesTab(IDiseaseDataProvider dataProvider) {
		VerticalLayout vl_genes = new VerticalLayout();

		Collection<DiseaseGene> genes = dataProvider.getAssociatedGenes();
		if (genes.size() < 1) {
			Label l = new Label("No genes associated with this disease.");
			l.addStyleName("tab-content-content");
			vl_genes.addComponent(l);
		}
		else {
			for (DiseaseGene gene : genes) {
				Label l = new Label(gene.getGeneSymbol() + " (<a href='" + CONSTANTS.rootLocation + "?" + CONSTANTS.geneRequestId + "="
						+ gene.getGeneId() + "'>" + gene.getGeneId() + "</a>)", ContentMode.HTML);
				l.addStyleName("tab-content-content");
				vl_genes.addComponent(l);
			}
		}

		vl_genes.addStyleName("tab-content-vl");
		return vl_genes;
	}

	private VerticalLayout getAnnotatedHpoClassesTab(IDiseaseDataProvider dataProvider) {
		VerticalLayout tableVL = new VerticalLayout();
		tableVL.setSizeFull();

		Table table = new Table();
		table.addContainerProperty("HPO id", TableLabel.class, null);
		table.addContainerProperty("HPO label", TableLabel.class, null);
		table.setSizeFull();

		List<HpoClassTableEntry> tableContent = dataProvider.getAnnotatedHpoClasses();

		int id = 0;
		for (HpoClassTableEntry entry : tableContent) {
			TableLabel hpoid = new TableLabel(
					"<a href='" + CONSTANTS.rootLocation + "?" + CONSTANTS.hpRequestId + "=" + entry.getHpoId() + "'>" + entry.getHpoId() + "</a>",
					ContentMode.HTML);
			TableLabel hpolabel = new TableLabel(entry.getHpoLabel(), ContentMode.HTML);
			hpoid.setDescription(entry.getDescription());
			hpolabel.setDescription(entry.getDescription());

			Integer itemId = new Integer(id++);
			table.addItem(new Object[] { hpoid, hpolabel }, itemId);
		}

		tableVL.addComponent(table);

		String diseaseId = dataProvider.getId();
		String header = "Export for " + diseaseId;
		String fileName = "hpoterms_for_" + diseaseId;
		tableUtils.addDownloadButtons(tableVL, table, fileName, header);
		tableVL.addStyleName("tab-content-vl");
		tableVL.setExpandRatio(table, 1f);
		return tableVL;
	}

	private VerticalLayout getAltNamesTab(IDiseaseDataProvider dataProvider) {
		VerticalLayout vl_syns = new VerticalLayout();

		Collection<String> synonyms = dataProvider.getAlternativeNames();
		if (synonyms.size() < 1) {
			Label l = new Label("No synonyms or alternative names");
			l.addStyleName("tab-content-content");
			vl_syns.addComponent(l);
		}
		else {
			for (String synoym : synonyms) {
				Label l = new Label(synoym);
				l.addStyleName("tab-content-content");
				vl_syns.addComponent(l);
			}
		}

		vl_syns.addStyleName("tab-content-vl");
		return vl_syns;
	}

	public void addDiseaseInfoElements(Container gridContainer, IDiseaseDataProvider dataProvider) {

		Row row1 = gridContainer.addRow();
		row1.setWidth("100%");
		/*
		 * Alt names / synonyms
		 */
		{
			VerticalLayout vl_altNames = getAltNamesTab(dataProvider);
			Col col1 = row1.addCol(ColMod.MD_4);
			col1.addComponent(vl_altNames);
			col1.setHeight("100%");
			col1.addStyleName("v-csslayout-gridelement");
		}

		/*
		 * Associated terms
		 */
		{
			VerticalLayout vl_terms = getAnnotatedHpoClassesTab(dataProvider);
			Col col1 = row1.addCol(ColMod.MD_4);
			col1.addComponent(vl_terms);
			col1.setHeight("100%");
			col1.addStyleName("v-csslayout-gridelement");
		}

		/*
		 * Associated genes
		 */

		{
			VerticalLayout vl_genes = getAssociatedGenesTab(dataProvider);
			Col col1 = row1.addCol(ColMod.MD_4);
			col1.addComponent(vl_genes);
			col1.setHeight("100%");
			col1.addStyleName("v-csslayout-gridelement");
		}

	}

}
