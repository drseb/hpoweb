package hpoweb.util;

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
import com.vaadin.ui.themes.ValoTheme;

import de.charite.phenowl.annotations.OwlAnnotatedDiseaseEntry;
import hpoweb.data.entities.DiseaseGene;

public class TableUtils {

	public void addDownloadButtons(VerticalLayout tableVL, final Table table, String filename, String header) {
		final ThemeResource exportExcel = new ThemeResource("img/table-excel-15px.png");
		final Button excelExportButton = new Button("Export to Excel");
		excelExportButton.setIcon(exportExcel);
		excelExportButton.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
		excelExportButton.addStyleName(ValoTheme.BUTTON_TINY);
		excelExportButton.addStyleName(ValoTheme.BUTTON_QUIET);

		excelExportButton.addClickListener(event -> {
			ExcelExport excelExport = new ExcelExport(table);
			excelExport.excludeCollapsedColumns();
			excelExport.setReportTitle(header);
			excelExport.setExportFileName(filename + ".xls");
			excelExport.export();
			excelExport = null;
		});

		final ThemeResource exportCsv = new ThemeResource("img/table-csv-15px.png");
		final Button csvExportButton = new Button("Export to CSV");
		csvExportButton.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
		csvExportButton.addStyleName(ValoTheme.BUTTON_TINY);
		csvExportButton.addStyleName(ValoTheme.BUTTON_QUIET);
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
		tableVL.setComponentAlignment(hlButtons, Alignment.BOTTOM_CENTER);
	}

	public String getGenesAsHtmlString(ArrayList<DiseaseGene> associatedGenes, String oldLocation) {
		ArrayList<String> elems = new ArrayList<String>();
		for (DiseaseGene g : associatedGenes) {
			String gStr = g.getGeneSymbol() + " (<a href='" + oldLocation + "?" + CONSTANTS.geneRequestId + "=" + g.getGeneId() + "'>" + g.getGeneId()
					+ "</a>)";
			elems.add(gStr);
		}

		return Joiner.on(", ").join(elems);
	}

	public String getDiseasesAsHtmlString(ArrayList<OwlAnnotatedDiseaseEntry> associatedDiseases, String oldLocation) {
		ArrayList<String> elems = new ArrayList<String>();
		for (OwlAnnotatedDiseaseEntry disease : associatedDiseases) {
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
