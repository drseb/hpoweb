package hpoweb;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import hpo.ItemId;
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

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = HpowebUI.class)
	public static class Servlet extends VaadinServlet {
	}

	private static String ontologyVersion;

	private GoogleAnalyticsTracker tracker;

	@Override
	protected void init(VaadinRequest request) {

		setSizeFull();
		/*
		 * Add google tracker
		 */
		addTracker();

		/*
		 * ************* Constant part comes here
		 */
		/*
		 * Init hpo data
		 */
		if (doParseHpo) {
			synchronized (block) {
				if (hpData == null)
					hpData = new HpData();
			}
		}

		if (doParseHpo) {
			ontologyVersion = hpData.getExtOwlOntology().getOntologyVersionIri().toString();
		}
		else {
			ontologyVersion = "some ontology version here";
		}

		/*
		 * Set the site url template
		 */
		String location = Page.getCurrent().getLocation().toString();
		String rootLocation = location.replaceAll("\\?.+", "");
		rootLocation = rootLocation.replaceAll("\\#.+", "");

		CONSTANTS.rootLocation = rootLocation;

		Page page = HpowebUI.get().getPage();
		String uriFragment = page.getUriFragment();
		if (uriFragment == null || uriFragment.equals("")) {
			putParametersIntoUriFragment(request);
		}
		uriFragment = page.getUriFragment();

		/************************
		 * Now the content displayed
		 */
		Container gridContainer = getContentContainer(uriFragment);
		setContent(gridContainer);

		page.addUriFragmentChangedListener(new UriFragmentChangedListener() {
			@Override
			public void uriFragmentChanged(UriFragmentChangedEvent source) {
				System.out.println("got a change in uri fragment here");
				Page page = HpowebUI.get().getPage();
				String uriFragment = page.getUriFragment();
				Container newGridContainer = getContentContainer(uriFragment);
				setContent(newGridContainer);
			}
		});

	}

	/**
	 * @param uriFragment
	 * @return
	 */
	public static Container getContentContainer(String uriFragment) {

		SearchBarFactory searchbarFactory = new SearchBarFactory();
		LazyComboBox<SearchableEntity> searchBar = searchbarFactory.getSearchBar(hpData);
		searchBar.setWidth("100%");

		Container gridContainer = new Container();

		gridContainer.removeAllComponents();

		// just a line that disappears on small devices
		addHorizontalLine(gridContainer);

		/*
		 * Add search bar on top
		 */
		Row r = gridContainer.addRow();
		Col c = r.addCol();
		c.addComponent(searchBar);

		// just a line that disappears on small devices
		addHorizontalLine(gridContainer);

		/*
		 * Data provider initialization
		 */
		IEntityDataProvider dataProvider = setupDataProvider(uriFragment);

		if (dataProvider == null)
			return gridContainer;

		addInfoLabels(gridContainer, dataProvider);

		// just a line that disappears on small devices
		addHorizontalLine(gridContainer);

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

		Label version = new Label("Ontology version: " + ontologyVersion);
		Label copyright = new Label("Copyright 2018 -  Sebastian Köhler & The Phenomics Group Berlin");
		Link feedback = new Link("Contact: dr.sebastian.koehler@gmail.com",
				new ExternalResource("http://phenomics.github.io/"));

		// just a line that disappears on small devices
		addHorizontalLine(gridContainer);
		addLabelRow(gridContainer, version);
		addLabelRow(gridContainer, copyright);
		addLabelRow(gridContainer, feedback);
		return gridContainer;
	}

	/**
	 * @param request
	 */
	private void putParametersIntoUriFragment(VaadinRequest request) {
		Page page = HpowebUI.get().getPage();

		if (request.getParameterMap().containsKey(CONSTANTS.hpRequestId)) {
			String hpId = request.getParameter(CONSTANTS.hpRequestId);
			if (hpId.contains(":"))
				hpId = hpId.replaceAll(":", "_");
			page.setUriFragment(CONSTANTS.hpRequestId + "=" + hpId, false);
		}
		else if (request.getParameterMap().containsKey(CONSTANTS.geneRequestId)) {
			String hpId = request.getParameter(CONSTANTS.geneRequestId);
			page.setUriFragment(CONSTANTS.geneRequestId + "=" + hpId, false);
		}
		else if (request.getParameterMap().containsKey(CONSTANTS.diseaseRequestId)) {
			String hpId = request.getParameter(CONSTANTS.diseaseRequestId);
			page.setUriFragment(CONSTANTS.diseaseRequestId + "=" + hpId, false);
		}
	}

	private static void addLabelRow(Container gridContainer, Component label) {
		Row r = gridContainer.addRow();
		Col c = r.addCol();
		c.addComponent(label);
		label.addStyleName(ValoTheme.LABEL_LIGHT);
		label.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		label.addStyleName(ValoTheme.LABEL_SMALL);
	}

	private static void addExtraButtons(Container gridContainer, IHpClassDataProvider dataProvider) {

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

	private static Button getGraphViewButton(IHpClassDataProvider dataProvider) {

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

	private static IEntityDataProvider setupDataProvider(String uriFragment) {

		if (uriFragment == null || uriFragment.equals(""))
			return null;

		System.out.println("got fragment: " + uriFragment);
		IEntityDataProvider dataProvider = null;
		Pattern hpPattern = Pattern.compile(CONSTANTS.hpRequestId + "=(HP.+)");
		Matcher hpMatcher = hpPattern.matcher(uriFragment);
		Pattern genePattern = Pattern.compile(CONSTANTS.geneRequestId + "=(\\d+)");
		Matcher geneMatcher = genePattern.matcher(uriFragment);
		Pattern diseasePattern = Pattern.compile(CONSTANTS.diseaseRequestId + "=(.+)");
		Matcher diseaseMatcher = diseasePattern.matcher(uriFragment);
		if (hpMatcher.find()) {

			OWLClass hpClass = parseHpId(hpMatcher.group(1));
			if (hpClass == null && doParseHpo) {
				new Notification("Invalid HPO id input", "<br/><br/>Can't parse HPO id from '" + uriFragment + "'",
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
		else if (geneMatcher.find()) {

			Integer geneId = parseGeneId(geneMatcher.group(1));
			if (geneId == null && doParseHpo) {
				new Notification("Invalid gene id input", "<br/><br/>Can't parse gene id from '" + uriFragment + "'",
						Notification.Type.ERROR_MESSAGE, true).show(Page.getCurrent());
				return null;
			}

			if (doParseHpo) {

				dataProvider = new GeneDataProvider(geneId, hpData);
			}
			else {
				dataProvider = new FakeGeneDataProvider();
			}

		}
		else if (diseaseMatcher.find()) {

			ItemId diseaseId = parseDiseaseId(diseaseMatcher.group(1));
			if (diseaseId == null && doParseHpo) {
				new Notification("Invalid disease id input",
						"<br/><br/>Can't parse disease id from '" + uriFragment + "'", Notification.Type.ERROR_MESSAGE,
						true).show(Page.getCurrent());
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
			new Notification("Invalid URL",
					"<br/><br/>You have to provide one URL parameter (" + CONSTANTS.hpRequestId + ","
							+ CONSTANTS.geneRequestId + ", or " + CONSTANTS.diseaseRequestId + ") ! ",
					Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
			return null;
		}
		return dataProvider;
	}

	private static void addInfoLabels(Container gridContainer, IEntityDataProvider dataProvider) {
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
	private static void addHorizontalLine(Container gridContainer) {
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
	private static Button getCopyPasteButton(final String id, final String label) {
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

	private static OWLClass parseHpId(String matchedPartOfUri) {

		if (!doParseHpo)
			return null;

		if (matchedPartOfUri == null || (!matchedPartOfUri.startsWith("HP"))) {
			return null;
		}

		OWLClass x = hpData.getExtOwlOntology().getClassForId(matchedPartOfUri);
		return x;
	}

	private static Integer parseGeneId(String matchedPartOfUri) {

		if (!doParseHpo)
			return null;

		Integer geneIdInt = null;
		try {
			geneIdInt = Integer.parseInt(matchedPartOfUri);
		} catch (NumberFormatException nfe) {
			return null;
		}

		if (!hpData.getAnnotationUtils().getDiseaseGeneMapper().entrezId2diseaseIds.containsKey(geneIdInt.intValue()))
			return null;

		return geneIdInt;
	}

	private static ItemId parseDiseaseId(String matchedPartOfUri) {
		if (!doParseHpo)
			return null;

		System.out.println("parse disease id form  " + matchedPartOfUri);
		ItemId diseaseId = new ItemId(matchedPartOfUri);
		if (!hpData.getAnnotationUtils().getDiseaseId2entry().containsKey(diseaseId)) {
			return null;
		}

		return diseaseId;
	}

	public static HpowebUI get() {
		return (HpowebUI) UI.getCurrent();
	}
}