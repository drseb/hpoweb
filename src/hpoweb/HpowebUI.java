package hpoweb;

import hpoweb.data.HpData;
import hpoweb.data.dataprovider.IDiseaseDataProvider;
import hpoweb.data.dataprovider.IEntityDataProvider;
import hpoweb.data.dataprovider.IGeneDataProvider;
import hpoweb.data.dataprovider.IHpClassDataProvider;
import hpoweb.data.dataprovider.impl.DiseaseDataProvider;
import hpoweb.data.dataprovider.impl.FakeDiseaseDataProvider;
import hpoweb.data.dataprovider.impl.FakeGeneDataProvider;
import hpoweb.data.dataprovider.impl.FakeHpClassDataProvider;
import hpoweb.data.dataprovider.impl.GeneDataProvider;
import hpoweb.data.dataprovider.impl.HpClassDataProvider;
import hpoweb.data.entities.SearchableEntity;
import hpoweb.uicontent.SearchBarFactory;
import hpoweb.uicontent.tabs.disease.DiseaseTabFactory;
import hpoweb.uicontent.tabs.gene.GeneTabFactory;
import hpoweb.uicontent.tabs.hpoclass.HpoClassTabFactory;
import hpoweb.util.CONSTANTS;
import hpoweb.util.TableUtils;

import java.util.Map;

import javax.servlet.annotation.WebServlet;

import org.semanticweb.owlapi.model.OWLClass;
import org.vaadin.googleanalytics.tracking.GoogleAnalyticsTracker;
import org.vaadin.viritin.fields.LazyComboBox;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.charite.phenowl.annotations.DiseaseId;

@SuppressWarnings("serial")
@Theme("hpoweb")
@Widgetset("hpoweb.widgetset.HpowebWidgetset")
public class HpowebUI extends UI {

	private static final boolean doParseHpo = true;
	private final static Object block = new Object();

	private static HpData hpData = null;
	private GoogleAnalyticsTracker tracker;

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = HpowebUI.class)
	public static class Servlet extends MyVaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {

		/*
		 * Init hpo data
		 */
		if (doParseHpo) {
			synchronized (block) {
				if (hpData == null)
					hpData = new HpData();
			}
		}

		/*
		 * Set the site url template
		 */
		String rootLocation = Page.getCurrent().getLocation().toString();
		if (rootLocation.contains("?")) {
			rootLocation = rootLocation.replaceAll("\\?.+", "");
		}
		CONSTANTS.rootLocation = rootLocation;

		Map<String, String[]> parameterMap = request.getParameterMap();

		final VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setSpacing(true);
		verticalLayout.setMargin(true);
		setContent(verticalLayout);

		/*
		 * Add search functionality on top
		 */
		SearchBarFactory searchbarFactory = new SearchBarFactory();
		LazyComboBox<SearchableEntity> searchBar = searchbarFactory.getSearchBar(hpData);
		verticalLayout.addComponent(searchBar);
		verticalLayout.setComponentAlignment(searchBar, Alignment.TOP_CENTER);
		searchBar.setWidth("100%");
		IEntityDataProvider dataProvider = null;

		tracker = new GoogleAnalyticsTracker("UA-62837903-2");
		addExtension(tracker);
		tracker.extend(UI.getCurrent());
		tracker.extend(this);
		tracker.trackPageview(Page.getCurrent().getLocation().toString());

		if (parameterMap.containsKey(CONSTANTS.hpRequestId)) {

			OWLClass hpClass = parseHpId(request);
			if (hpClass == null && doParseHpo) {
				new Notification("Invalid HPO id input", "<br/><br/>Can't parse HPO id from '" + request.getParameter(CONSTANTS.hpRequestId) + "'",
						Notification.Type.ERROR_MESSAGE, true).show(Page.getCurrent());
				return;
			}

			if (doParseHpo) {
				dataProvider = new HpClassDataProvider(hpClass, hpData);
			}
			else {
				dataProvider = new FakeHpClassDataProvider();
			}

		}
		else if (parameterMap.containsKey(CONSTANTS.geneRequestId)) {

			Integer geneId = parseGeneId(request);
			if (geneId == null && doParseHpo) {
				new Notification("Invalid gene id input", "<br/><br/>Can't parse gene id from '" + request.getParameter(CONSTANTS.geneRequestId)
						+ "'", Notification.Type.ERROR_MESSAGE, true).show(Page.getCurrent());
				return;
			}

			if (doParseHpo) {

				dataProvider = new GeneDataProvider(geneId, hpData);
			}
			else {
				dataProvider = new FakeGeneDataProvider();
			}

		}
		else if (parameterMap.containsKey(CONSTANTS.diseaseRequestId)) {

			DiseaseId diseaseId = parseDiseaseId(request);
			if (diseaseId == null && doParseHpo) {
				new Notification("Invalid disease id input", "<br/><br/>Can't parse disease id from '"
						+ request.getParameter(CONSTANTS.geneRequestId) + "'", Notification.Type.ERROR_MESSAGE, true).show(Page.getCurrent());
				return;
			}

			if (doParseHpo) {

				dataProvider = new DiseaseDataProvider(diseaseId, hpData);
			}
			else {
				dataProvider = new FakeDiseaseDataProvider();
			}
		}
		else {
			new Notification("Invalid URL", "<br/><br/>You have to provide one URL parameter (" + CONSTANTS.hpRequestId + ","
					+ CONSTANTS.geneRequestId + ", or " + CONSTANTS.diseaseRequestId + ") ! ", Notification.Type.WARNING_MESSAGE, true).show(Page
					.getCurrent());
			return;
		}

		VerticalLayout vl = new VerticalLayout();
		// hl.setSpacing(false);
		Label info1 = new Label("Infopage for " + dataProvider.getTypeOfEntityString());
		Label info2 = new Label(dataProvider.getLabel());
		info1.addStyleName(ValoTheme.LABEL_LIGHT);
		info2.addStyleName(ValoTheme.LABEL_H2);
		info1.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		info2.addStyleName(ValoTheme.LABEL_NO_MARGIN);

		vl.addComponent(info1);
		vl.addComponent(info2);
		verticalLayout.addComponent(vl);

		TabSheet sheet = new TabSheet();
		sheet.addStyleName(ValoTheme.TABSHEET_FRAMED);
		sheet.addStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
		sheet.setSizeFull();

		TableUtils tableUtils = new TableUtils();

		if (dataProvider instanceof IHpClassDataProvider) {

			HpoClassTabFactory hpoClassTabFactory = new HpoClassTabFactory(hpData, tableUtils);
			hpoClassTabFactory.addTermInfoTabs(sheet, (IHpClassDataProvider) dataProvider);

		}
		else if (dataProvider instanceof IDiseaseDataProvider) {

			DiseaseTabFactory diseaseTabFactory = new DiseaseTabFactory(tableUtils);
			diseaseTabFactory.addDiseaseInfoTabs(sheet, (IDiseaseDataProvider) dataProvider);

		}
		else if (dataProvider instanceof IGeneDataProvider) {

			GeneTabFactory geneTabFactory = new GeneTabFactory(tableUtils);
			geneTabFactory.addGeneInfoTabs(sheet, (IGeneDataProvider) dataProvider);
		}

		verticalLayout.addComponent(sheet);
		verticalLayout.setExpandRatio(sheet, 1f);
		verticalLayout.setSizeFull();

		Label copyright = new Label("Copyright 2015 -  The Human Phenotype Ontology Project");
		copyright.addStyleName(ValoTheme.LABEL_LIGHT);
		copyright.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		copyright.addStyleName(ValoTheme.LABEL_SMALL);
		verticalLayout.addComponent(copyright);

	}

	private OWLClass parseHpId(VaadinRequest request) {

		if (!doParseHpo)
			return null;

		String hpId = request.getParameter(CONSTANTS.hpRequestId);
		if (hpId == null || (!hpId.startsWith("HP"))) {
			return null;
		}

		OWLClass x = hpData.getExtOwlOntology().getClassForId(hpId);
		return x;
	}

	private Integer parseGeneId(VaadinRequest request) {

		if (!doParseHpo)
			return null;

		String geneId = request.getParameter(CONSTANTS.geneRequestId);
		Integer geneIdInt = null;
		try {
			geneIdInt = Integer.parseInt(geneId);
		} catch (NumberFormatException nfe) {
			return null;
		}

		if (!hpData.getAnnotationUtils().getDiseaseGeneMapper().entrezId2diseaseIds.containsKey(geneIdInt.intValue()))
			return null;

		return geneIdInt;
	}

	private DiseaseId parseDiseaseId(VaadinRequest request) {
		if (!doParseHpo)
			return null;
		String diseaseIdStr = request.getParameter(CONSTANTS.diseaseRequestId);

		DiseaseId diseaseId = new DiseaseId(diseaseIdStr);
		if (!hpData.getAnnotationUtils().getDiseaseId2entry().containsKey(diseaseId)) {
			return null;
		}

		return diseaseId;
	}
}