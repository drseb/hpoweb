package hpoweb.uicontent.tabs.hpoclass;

import hpoweb.data.HpData;
import hpoweb.data.dataprovider.IHpClassDataProvider;
import hpoweb.uicontent.graph.GraphtestUI;
import hpoweb.uicontent.table.DiseaseGeneTableEntry;
import hpoweb.uicontent.table.GeneDiseaseTableEntry;
import hpoweb.uicontent.table.TableLabel;
import hpoweb.util.CONSTANTS;
import hpoweb.util.TableUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.semanticweb.owlapi.model.OWLClass;

import com.google.common.base.Joiner;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.charite.phenowl.hpowl.util.OboUtil;

public class HpoClassTabFactory {

	private HpData hpData;
	private TableUtils tableUtils;

	public HpoClassTabFactory(HpData hpData, TableUtils tableUtils) {
		this.hpData = hpData;
		this.tableUtils = tableUtils;
	}

	public void addTermInfoTabs(TabSheet sheet, IHpClassDataProvider dataProvider) {

		/*
		 * IDs
		 */
		VerticalLayout vl_ids = getIdentifierTab(dataProvider);
		sheet.addTab(vl_ids, "Identifier");

		/*
		 * Synonyms
		 */

		VerticalLayout l2 = getSynonymTab(dataProvider);
		sheet.addTab(l2, "Synonyms");

		/*
		 * Textdef
		 */
		VerticalLayout l3 = getDefinitionsTab(dataProvider);
		sheet.addTab(l3, "Definition");

		/*
		 * Subclasses + Superclasses
		 */
		VerticalLayout l4 = getSubSuperClassesTab(dataProvider);
		sheet.addTab(l4, "Sub- and superclasses");

		/*
		 * Graph stuff
		 */
		VerticalLayout l5 = getGraphTab(dataProvider);
		sheet.addTab(l5, "Graph");

		/*
		 * Associated diseases
		 */
		VerticalLayout l6 = getAssociatedDiseasesTab(dataProvider);
		sheet.addTab(l6, "Diseases");

		/*
		 * Associated genes
		 */
		VerticalLayout l7 = getAssociatedGenesTab(dataProvider);
		sheet.addTab(l7, "Genes");

	}

	private VerticalLayout getAssociatedGenesTab(IHpClassDataProvider dataProvider) {

		VerticalLayout tableVL = new VerticalLayout();
		tableVL.setSizeFull();

		Table table = new Table();
		table.addContainerProperty("Gene", TableLabel.class, null);
		table.addContainerProperty("Associated diseases", TableLabel.class, null);
		table.setSizeFull();

		List<GeneDiseaseTableEntry> tableContent = dataProvider.getGeneDiseaseTableContent();

		int id = 0;
		for (GeneDiseaseTableEntry entry : tableContent) {
			TableLabel geneEntry = new TableLabel(entry.getGeneSymbol() + " (<a href='" + CONSTANTS.rootLocation + "?" + CONSTANTS.geneRequestId
					+ "=" + entry.getGeneId() + "'>" + entry.getGeneId() + "</a>)", ContentMode.HTML);

			String diseasesString = tableUtils.getDiseasesAsHtmlString(entry.getAssociatedDiseases(), CONSTANTS.rootLocation);

			TableLabel diseases = new TableLabel(diseasesString, ContentMode.HTML);
			Integer itemId = new Integer(id++);
			table.addItem(new Object[] { geneEntry, diseases }, itemId);
		}

		tableVL.addComponent(table);
		tableUtils.addDownloadButtons(tableVL, table);

		tableVL.setExpandRatio(table, 1f);
		tableVL.addStyleName("tab-content-vl");
		return tableVL;
	}

	private VerticalLayout getAssociatedDiseasesTab(IHpClassDataProvider dataProvider) {
		VerticalLayout tableVL = new VerticalLayout();
		tableVL.setSizeFull();

		Table table = new Table();
		table.addContainerProperty("Disease id", TableLabel.class, null);
		table.addContainerProperty("Disease name", TableLabel.class, null);
		table.addContainerProperty("Associated genes", TableLabel.class, null);
		table.setSizeFull();

		List<DiseaseGeneTableEntry> tableContent = dataProvider.getDiseaseGeneTableContent();

		int id = 0;
		for (DiseaseGeneTableEntry entry : tableContent) {
			TableLabel diseaseid = new TableLabel("<a href='" + CONSTANTS.rootLocation + "?" + CONSTANTS.diseaseRequestId + "="
					+ entry.getDiseaseId() + "'>" + entry.getDiseaseId() + "</a>", ContentMode.HTML);
			TableLabel diseasename = new TableLabel(entry.getDiseaseName(), ContentMode.HTML);

			String genesString = tableUtils.getGenesAsHtmlString(entry.getAssociatedGenes(), CONSTANTS.rootLocation);

			TableLabel genes = new TableLabel(genesString, ContentMode.HTML);
			Integer itemId = new Integer(id++);
			table.addItem(new Object[] { diseaseid, diseasename, genes }, itemId);
		}

		tableVL.addComponent(table);

		tableUtils.addDownloadButtons(tableVL, table);
		tableVL.addStyleName("tab-content-vl");
		tableVL.setExpandRatio(table, 1f);
		return tableVL;
	}

	private VerticalLayout getSynonymTab(IHpClassDataProvider dataProvider) {

		VerticalLayout vl_syns = new VerticalLayout();

		Collection<String> synonyms = dataProvider.getSynonyms();
		if (synonyms.size() < 1) {
			Label suggestSyn = new Label("Currently we do not have synonyms for this class. If you are missing a synonym, feel free to suggest a "
					+ "synonym at our <a href='https://github.com/obophenotype/human-phenotype-ontology/issues/' target='_new'>github tracker</a>",
					ContentMode.HTML);
			suggestSyn.addStyleName("tab-content-content");
			vl_syns.addComponent(suggestSyn);
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

	private VerticalLayout getIdentifierTab(IHpClassDataProvider dataProvider) {

		VerticalLayout vl_ids = new VerticalLayout();
		/*
		 * Primary ID
		 */
		Label lab1 = new Label("Primary ID");
		lab1.setDescription("This is the primary identifier for this HPO class.");
		lab1.addStyleName(ValoTheme.LABEL_LIGHT);
		lab1.addStyleName("tab-content-header");
		Label prim1 = new Label(dataProvider.getPrimaryId());
		prim1.addStyleName("tab-content-content");
		vl_ids.addComponent(lab1);
		vl_ids.addComponent(prim1);

		/*
		 * Secondary ID
		 */
		Label lab2 = new Label("Alternative IDs");
		lab2.setDescription("These are other identifiers which are referring to the same HPO class. "
				+ "These are introduced when classes are merged.");
		lab2.addStyleName(ValoTheme.LABEL_LIGHT);
		lab2.addStyleName("tab-content-header");

		vl_ids.addComponent(lab2);
		String altIds = Joiner.on(", ").join(dataProvider.getAlternativeIds());
		if (altIds.equals(""))
			altIds = "-";
		Label altIdLab = new Label(altIds);
		altIdLab.addStyleName("tab-content-content");
		vl_ids.addComponent(altIdLab);

		/*
		 * PURLs
		 */
		Label lab3 = new Label("PURL");
		lab3.setDescription("This is a persistent URL. Please see <a href='https://en.wikipedia.org/wiki/Persistent_uniform_resource_locator' target='_new'>"
				+ "this wikipedia article</a> for more information on PURLs.");
		lab3.addStyleName(ValoTheme.LABEL_LIGHT);
		lab3.addStyleName("tab-content-header");
		Label iriLabel = new Label("<a href='" + dataProvider.getIRI() + "' target='_new'>" + dataProvider.getIRI() + "</a>", ContentMode.HTML);
		iriLabel.addStyleName("tab-content-content");
		vl_ids.addComponent(lab3);
		vl_ids.addComponent(iriLabel);

		// layout of content
		vl_ids.addStyleName("tab-content-vl");
		return vl_ids;
	}

	private VerticalLayout getDefinitionsTab(IHpClassDataProvider dataProvider) {

		VerticalLayout vl_ids = new VerticalLayout();

		/*
		 * Textdef
		 */
		Label lab1 = new Label("Textual definition");
		lab1.setDescription("This is a human readable textual definition of the HPO class.");
		lab1.addStyleName(ValoTheme.LABEL_LIGHT);
		lab1.addStyleName("tab-content-header");

		String textdef = dataProvider.getTextdef();
		if (textdef == null)
			textdef = "Currently we do not have textual definition for this class. Feel free to suggest a definition at "
					+ "our <a href='https://github.com/obophenotype/human-phenotype-ontology/issues/'>github tracker</a>.";

		Label labelTextdef = new Label(textdef, ContentMode.HTML);
		labelTextdef.addStyleName("tab-content-content");
		vl_ids.addComponent(lab1);
		vl_ids.addComponent(labelTextdef);

		/*
		 * Logical def
		 */
		Label lab2 = new Label("Logical definition");
		lab2.setDescription("This is a computer readable logical definition of the HPO class. If you want to learn more about this please see <a href='http://nar.oxfordjournals.org/content/42/D1/D966.full' target='_new'>http://nar.oxfordjournals.org/content/42/D1/D966.full</a>.");
		lab2.addStyleName(ValoTheme.LABEL_LIGHT);
		lab2.addStyleName("tab-content-header");

		String logicalDef = dataProvider.getLogicalDef();
		if (logicalDef == null)
			logicalDef = "Currently we do not have logical definition for this class. Feel free to suggest "
					+ "a logical definition at our <a href='https://github.com/obophenotype/human-phenotype-ontology/issues/' target='_new'>github tracker</a>.";

		Label labelLogicaldef = new Label(logicalDef, ContentMode.HTML);
		labelLogicaldef.addStyleName("tab-content-content");
		vl_ids.addComponent(lab2);
		vl_ids.addComponent(labelLogicaldef);

		vl_ids.addStyleName("tab-content-vl");
		return vl_ids;
	}

	private VerticalLayout getGraphTab(IHpClassDataProvider dataProvider) {
		VerticalLayout l5 = new VerticalLayout();
		l5.setSizeFull();
		GraphtestUI ui = dataProvider.getGraphtestUi();
		l5.addComponent(ui.getGraphComponent());
		return l5;
	}

	private VerticalLayout getSubSuperClassesTab(IHpClassDataProvider dataProvider) {
		VerticalLayout vlSubSuperClasses = new VerticalLayout();

		Collection<OWLClass> superClasses = dataProvider.getSuperClasses();
		Collection<OWLClass> subClasses = dataProvider.getSubClasses();

		Collection<String> superClassesHtmlString = convertToHtml(superClasses, CONSTANTS.rootLocation);
		Collection<String> subClassesHtmlString = convertToHtml(subClasses, CONSTANTS.rootLocation);

		addLabelsSupSub(superClassesHtmlString, "Superclasses", vlSubSuperClasses);
		addLabelsSupSub(subClassesHtmlString, "Subclasses", vlSubSuperClasses);

		// /*
		// * Superclasses
		// */
		// Label lab1 = new Label("Superclasses");
		// lab1.addStyleName(ValoTheme.LABEL_LIGHT);
		// lab1.addStyleName("tab-content-header");
		//
		// Label labelSuperclasses = new Label(Joiner.on(", ").join(superClassesHtmlString), ContentMode.HTML);
		// labelSuperclasses.addStyleName("tab-content-content");
		// vlSubSuperClasses.addComponent(lab1);
		// vlSubSuperClasses.addComponent(labelSuperclasses);
		//
		// /*
		// * Subclasses
		// */
		// Label lab2 = new Label("Subclasses");
		// lab2.addStyleName(ValoTheme.LABEL_LIGHT);
		// lab2.addStyleName("tab-content-header");
		// Label labelSubClasses;
		// if (subClassesHtmlString.size() < 1) {
		// labelSubClasses = new Label("Currently, there is no subclass for this class. If you want to suggest a subclass, please use our "
		// + "<a href='https://github.com/obophenotype/human-phenotype-ontology/issues/' target='_new'>github tracker</a>.",
		// ContentMode.HTML);
		// }
		// else {
		// labelSubClasses = new Label(Joiner.on(", ").join(subClassesHtmlString), ContentMode.HTML);
		// }
		// labelSubClasses.addStyleName("tab-content-content");
		// vlSubSuperClasses.addComponent(lab2);
		// vlSubSuperClasses.addComponent(labelSubClasses);

		// layout of content
		vlSubSuperClasses.addStyleName("tab-content-vl");
		return vlSubSuperClasses;
	}

	private void addLabelsSupSub(Collection<String> superClassesHtmlString, String string, VerticalLayout vlSubSuperClasses) {
		/*
		 * Super or sub classes
		 */
		Label lab1 = new Label(string);
		lab1.addStyleName(ValoTheme.LABEL_LIGHT);
		lab1.addStyleName("tab-content-header");
		vlSubSuperClasses.addComponent(lab1);
		for (String l : superClassesHtmlString) {
			Label labelSuperclasses = new Label(l, ContentMode.HTML);
			labelSuperclasses.addStyleName("tab-content-content");
			vlSubSuperClasses.addComponent(labelSuperclasses);
		}

	}

	private Collection<String> convertToHtml(Collection<OWLClass> classes, String oldLocation) {

		Collection<String> htmlList = new HashSet<String>();
		if (classes == null) {
			for (int i = 0; i < 5; i++) {
				String id = "HP:" + RandomStringUtils.randomNumeric(7);
				String str = "<a href='" + oldLocation + "?" + CONSTANTS.hpRequestId + "=" + id + "'>"
						+ RandomStringUtils.randomAlphabetic(10).toUpperCase() + "</a>";
				htmlList.add(str);
			}
		}
		else {
			// it's ugly to use hpData here, but the dataprovider doesn't know
			// the labels for the parents/children
			for (OWLClass c : classes) {
				htmlList.add("<a href='" + oldLocation + "?" + CONSTANTS.hpRequestId + "=" + OboUtil.IRI2ID(c.getIRI()) + "'>"
						+ hpData.getExtOwlOntology().getLabel(c.getIRI()) + "</a>");
			}
		}

		return htmlList;
	}

}
