package hpoweb.util;

import hpoweb.data.entities.DiseaseGene;

import java.util.ArrayList;

import com.google.common.base.Joiner;
import com.vaadin.addon.tableexport.CsvExport;
import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import de.charite.phenowl.annotations.DiseaseEntry;

public class TableUtils {

	public void addDownloadButtons(VerticalLayout tableVL, final Table table, String filename, String header) {
		final ThemeResource exportExcel = new ThemeResource("img/table-excel-20px.png");
		final Button excelExportButton = new Button("Export to Excel");
		excelExportButton.setIcon(exportExcel);

		excelExportButton.addClickListener(event -> {
			ExcelExport excelExport = new ExcelExport(table);
			excelExport.excludeCollapsedColumns();
			excelExport.setReportTitle(header);
			excelExport.setExportFileName(filename + ".xls");
			excelExport.export();
			excelExport = null;
		});

		final ThemeResource exportCsv = new ThemeResource("img/table-csv-20px.png");
		final Button csvExportButton = new Button("Export to CSV");
		csvExportButton.setIcon(exportCsv);

		csvExportButton.addClickListener(event -> {
			CsvExport csvExport = new CsvExport(table);
			csvExport.excludeCollapsedColumns();
			csvExport.setReportTitle(header);
			csvExport.setExportFileName(filename + ".csv");
			csvExport.export();
			csvExport = null;
		});

		HorizontalLayout hlButtons = new HorizontalLayout();
		hlButtons.addComponent(excelExportButton);
		hlButtons.addComponent(csvExportButton);

		tableVL.addComponent(hlButtons);
		tableVL.setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
	}

	public String getGenesAsHtmlString(ArrayList<DiseaseGene> associatedGenes, String oldLocation) {
		ArrayList<String> elems = new ArrayList<String>();
		for (DiseaseGene g : associatedGenes) {
			String gStr = g.getGeneSymbol() + " (<a href='" + oldLocation + "?" + CONSTANTS.geneRequestId + "=" + g.getGeneId() + "'>"
					+ g.getGeneId() + "</a>)";
			elems.add(gStr);
		}

		return Joiner.on(", ").join(elems);
	}

	public String getDiseasesAsHtmlString(ArrayList<DiseaseEntry> associatedDiseases, String oldLocation) {
		ArrayList<String> elems = new ArrayList<String>();
		for (DiseaseEntry disease : associatedDiseases) {
			String nameShort = disease.getName();
			if (nameShort.length() > 40)
				nameShort = nameShort.substring(0, 40) + "...";
			String diseaseStr = nameShort + " (<a href='" + oldLocation + "?" + CONSTANTS.diseaseRequestId + "=" + disease.getDiseaseIdAsString()
					+ "'>" + disease.getDiseaseIdAsString() + "</a>)";
			elems.add(diseaseStr);
		}

		return Joiner.on(", ").join(elems);
	}

}
