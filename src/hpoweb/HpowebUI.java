package hpoweb;

import java.util.Map;

import javax.servlet.annotation.WebServlet;

import org.semanticweb.owlapi.model.OWLClass;
import org.vaadin.googleanalytics.tracking.GoogleAnalyticsTracker;
import org.vaadin.viritin.fields.LazyComboBox;

import com.sebworks.vaadstrap.Col;
import com.sebworks.vaadstrap.ColMod;
import com.sebworks.vaadstrap.Container;
import com.sebworks.vaadstrap.Row;
import com.sebworks.vaadstrap.VisibilityMod;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Viewport;
import com.vaadin.annotations.Widgetset;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.jsclipboard.JSClipboard;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.charite.phenowl.annotations.DiseaseId;
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
import hpoweb.uicontent.graph.GraphtestUI;
import hpoweb.uicontent.tabs.disease.DiseaseTabFactory;
import hpoweb.uicontent.tabs.gene.GeneTabFactory;
import hpoweb.uicontent.tabs.hpoclass.HpoClassTabFactory;
import hpoweb.util.CONSTANTS;
import hpoweb.util.TableUtils;

@SuppressWarnings("serial")
@Theme("hpoweb")
@Widgetset("hpoweb.widgetset.HpowebWidgetset")
@Viewport("width=device-width, initial-scale=1")
public class HpowebUI extends UI {

	private static final boolean doParseHpo = true;
	private final static Object block = new Object();

	private static HpData hpData = null;
	private GoogleAnalyticsTracker tracker;

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = HpowebUI.class)
	public static class Servlet extends VaadinServlet {
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

		setSizeFull();

		Container gridContainer = new Container();

		setContent(gridContainer);

		// just a line that disappears on small devices
		addLineRow(gridContainer);

		/*
		 * Add search bar on top
		 */
		addSearchbar(gridContainer);

		/*
		 * Add google tracker
		 */
		addTracker();

		// just a line that disappears on small devices
		addLineRow(gridContainer);

		/*
		 * Data provider initialization
		 */
		IEntityDataProvider dataProvider = setupDataProvider(request, parameterMap);
		if (dataProvider == null)
			return;

		addInfoLabels(gridContainer, dataProvider);

		// just a line that disappears on small devices
		addLineRow(gridContainer);

		TableUtils tableUtils = new TableUtils();

		if (dataProvider instanceof IHpClassDataProvider) {

			HpoClassTabFactory hpoClassTabFactory = new HpoClassTabFactory(hpData, tableUtils);
			hpoClassTabFactory.addTermInfoElements(gridContainer, (IHpClassDataProvider) dataProvider);

			addExtraButtons(gridContainer, (IHpClassDataProvider) dataProvider);
		}
		else if (dataProvider instanceof IDiseaseDataProvider) {

			DiseaseTabFactory diseaseTabFactory = new DiseaseTabFactory(tableUtils);
			diseaseTabFactory.addDiseaseInfoElements(gridContainer, (IDiseaseDataProvider) dataProvider);
		}
		else if (dataProvider instanceof IGeneDataProvider) {

			GeneTabFactory geneTabFactory = new GeneTabFactory(tableUtils);
			geneTabFactory.addGeneInfoElements(gridContainer, (IGeneDataProvider) dataProvider);

		}

		// just a line that disappears on small devices
		addLineRow(gridContainer);

		String ontologyVersion;
		if (doParseHpo) {
			ontologyVersion = hpData.getExtOwlOntology().getOntologyVersionIri().toString();
		}
		else

		{
			ontologyVersion = "some ontology version here";
		}

		/*
		 * Bottom part
		 */
		Label version = new Label("Ontology version: " + ontologyVersion);
		Label copyright = new Label("Copyright 2015 -  The Human Phenotype Ontology Project");
		Label feedback = new Label("Question, Comments, Feedback: sebastian.koehler@charite.de");
		addLabelRow(gridContainer, version);
		addLabelRow(gridContainer, copyright);
		addLabelRow(gridContainer, feedback);

	}

	private void addLabelRow(Container gridContainer, Label label) {
		Row r = gridContainer.addRow();
		Col c = r.addCol();
		c.addComponent(label);
		label.addStyleName(ValoTheme.LABEL_LIGHT);
		label.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		label.addStyleName(ValoTheme.LABEL_SMALL);
	}

	private void addExtraButtons(Container gridContainer, IHpClassDataProvider dataProvider) {

		Row r = gridContainer.addRow();
		r.setWidth("100%");

		/*
		 * Copypaste
		 */
		Button cpButton = getCopyPasteButton(dataProvider.getId(), dataProvider.getLabel());
		VerticalLayout vlCp = new VerticalLayout();
		vlCp.addComponent(cpButton);

		Col c1 = r.addCol(ColMod.SM_6);
		c1.addComponent(vlCp);
		c1.addStyleName("v-csslayout-gridelement");

		/*
		 * Graph
		 */
		Button graphButton = getGraphViewButton(dataProvider);
		VerticalLayout vlGraph = new VerticalLayout();
		vlGraph.addComponent(graphButton);
		Col c2 = r.addCol(ColMod.SM_6);
		c2.addComponent(vlGraph);
		c2.addStyleName("v-csslayout-gridelement");

	}

	private Button getGraphViewButton(IHpClassDataProvider dataProvider) {

		Button b = new Button("Graph view");
		b.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				GraphtestUI ui = dataProvider.getGraphtestUi();
				Window subWindow = new Window("Graph view");
				VerticalLayout subContent = new VerticalLayout();
				subContent.setMargin(true);
				subWindow.setContent(subContent);

				// Put some components in it
				subContent.addComponent(ui.getGraphComponent());
				subContent.setSizeFull();
				// Center it in the browser window
				subWindow.center();
				subWindow.setWidth("90%");
				subWindow.setHeight("90%");

				UI.getCurrent().addWindow(subWindow);
			}
		});

		return b;
	}

	private IEntityDataProvider setupDataProvider(VaadinRequest request, Map<String, String[]> parameterMap) {
		IEntityDataProvider dataProvider = null;

		if (parameterMap.containsKey(CONSTANTS.hpRequestId)) {

			OWLClass hpClass = parseHpId(request);
			if (hpClass == null && doParseHpo) {
				new Notification("Invalid HPO id input", "<br/><br/>Can't parse HPO id from '" + request.getParameter(CONSTANTS.hpRequestId) + "'",
						Notification.Type.ERROR_MESSAGE, true).show(Page.getCurrent());
				return null;
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
				new Notification("Invalid gene id input",
						"<br/><br/>Can't parse gene id from '" + request.getParameter(CONSTANTS.geneRequestId) + "'", Notification.Type.ERROR_MESSAGE,
						true).show(Page.getCurrent());
				return null;
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
				new Notification("Invalid disease id input",
						"<br/><br/>Can't parse disease id from '" + request.getParameter(CONSTANTS.geneRequestId) + "'",
						Notification.Type.ERROR_MESSAGE, true).show(Page.getCurrent());
				return null;
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
					+ CONSTANTS.geneRequestId + ", or " + CONSTANTS.diseaseRequestId + ") ! ", Notification.Type.WARNING_MESSAGE, true)
							.show(Page.getCurrent());
			return null;
		}
		return dataProvider;
	}

	private void addSearchbar(Container gridContainer) {
		SearchBarFactory searchbarFactory = new SearchBarFactory();
		LazyComboBox<SearchableEntity> searchBar = searchbarFactory.getSearchBar(hpData);
		searchBar.setWidth("100%");
		Row r = gridContainer.addRow();
		Col c = r.addCol();
		c.addComponent(searchBar);
	}

	private void addInfoLabels(Container gridContainer, IEntityDataProvider dataProvider) {
		Label info1 = new Label("Infopage for " + dataProvider.getTypeOfEntityString());
		Label info2 = new Label(dataProvider.getLabel());
		info1.addStyleName(ValoTheme.LABEL_LIGHT);
		info2.addStyleName(ValoTheme.LABEL_H2);
		info1.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		info2.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		Row row1 = gridContainer.addRow();
		row1.setWidth("100%");
		Col col12 = row1.addCol(ColMod.MD_4);
		Col col22 = row1.addCol(ColMod.MD_8);
		col12.addComponent(info1);
		col22.addComponent(info2);
	}

	/**
	 * Hidden on small devices
	 * 
	 * @param gridContainer
	 */
	private void addLineRow(Container gridContainer) {
		Row row1 = gridContainer.addRow();
		row1.setWidth("100%");
		Col col12 = row1.addCol(VisibilityMod.HIDDEN_SM, VisibilityMod.HIDDEN_XS);
		col12.addComponent(new Label("<hr />", ContentMode.HTML));

	}

	private void addTracker() {
		tracker = new GoogleAnalyticsTracker("UA-62837903-2");
		addExtension(tracker);
		tracker.extend(UI.getCurrent());
		tracker.extend(this);
		tracker.trackPageview(Page.getCurrent().getLocation().toString());
	}

	/**
	 * Not sure this is really elegant ;-)
	 * 
	 */
	private Button getCopyPasteButton(final String id, final String label) {
		final JSClipboard clipboard = new JSClipboard();

		Button b = new Button("Copy Id/Label");
		b.addFocusListener(new FocusListener() {

			@Override
			public void focus(FocusEvent event) {
				clipboard.setText(id + "\t" + label);
				clipboard.apply(b);
			}
		});
		b.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				clipboard.setText(id + "\t" + label);
				clipboard.apply(b);
			}
		});

		clipboard.apply(b);

		clipboard.addSuccessListener(new JSClipboard.SuccessListener() {

			@Override
			public void onSuccess() {
				Notification.show("Copy to clipboard successful");
			}
		});
		clipboard.addErrorListener(new JSClipboard.ErrorListener() {

			@Override
			public void onError() {
				Notification.show("Copy to clipboard unsuccessful", Notification.Type.ERROR_MESSAGE);
			}
		});

		return b;
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