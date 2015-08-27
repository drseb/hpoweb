package hpoweb.uicontent.graph;

import hpoweb.data.HpData;

import org.apache.commons.lang3.RandomStringUtils;
import org.semanticweb.owlapi.model.OWLClass;

import com.google.common.collect.ImmutableSet;
import com.vaadin.graph.GraphController;
import com.vaadin.graph.GraphExplorer;
import com.vaadin.graph.LayoutEngine;
import com.vaadin.ui.HorizontalLayout;

import de.charite.phenowl.hpowl.util.OboUtil;

public class GraphtestUI {
	private OWLClass hpClass;
	private HpData hpData;
	private boolean isFake;

	public GraphtestUI(OWLClass hpClass, HpData hpData) {
		this.hpClass = hpClass;
		this.hpData = hpData;
	}

	/**
	 * A fake instance. No real data used
	 */
	public GraphtestUI() {
		isFake = true;
	}

	public HorizontalLayout getGraphComponent() {

		HorizontalLayout layout = new HorizontalLayout();

		layout.setSizeFull();
		layout.removeAllComponents();
		SimpleGraphRepositoryImpl graphRepo = createGraphRepository();
		GraphController<NodeImpl, ArcImpl> graphController = new GraphController<NodeImpl, ArcImpl>();
		LayoutEngine engine = new JungDAGLayoutEngine();
		GraphExplorer<NodeImpl, ArcImpl> graph = new GraphExplorer<NodeImpl, ArcImpl>(graphRepo, graphController, engine);
		graph.setSizeFull();
		layout.addComponent(graph);

		return layout;
	}

	private SimpleGraphRepositoryImpl createGraphRepository() {
		SimpleGraphRepositoryImpl repo = new SimpleGraphRepositoryImpl();

		String classLabel;
		String classId;
		if (!isFake) {
			classLabel = hpData.getExtOwlOntology().getLabel(hpClass.getIRI());
			classId = OboUtil.IRI2ID(hpClass.getIRI());
		}
		else {
			classLabel = RandomStringUtils.randomAlphabetic(12);
			classId = "HP:" + RandomStringUtils.randomNumeric(7);
		}

		repo.addNode(classId, classLabel).setStyle("root");
		repo.setHomeNodeId(classId);

		if (!isFake) {
			ImmutableSet<OWLClass> superclasses = hpData.getExtOwlOntology().getParents(hpClass);
			ImmutableSet<OWLClass> subclasses = hpData.getExtOwlOntology().getChildren(hpClass);

			for (OWLClass superclass : superclasses) {

				String supLabel = hpData.getExtOwlOntology().getLabel(superclass.getIRI());
				String supClassId = OboUtil.IRI2ID(superclass.getIRI());

				repo.addNode(supClassId, supLabel).setStyle("superclass");
				repo.joinNodes(classId, supClassId, "edge_" + classId + "_" + supClassId, "subclass of");

				ImmutableSet<OWLClass> superclasses2 = hpData.getExtOwlOntology().getParents(superclass);

				for (OWLClass supSupClass : superclasses2) {
					String supsupLabel = hpData.getExtOwlOntology().getLabel(supSupClass.getIRI());
					String supsupClassId = OboUtil.IRI2ID(supSupClass.getIRI());

					repo.addNode(supsupClassId, supsupLabel).setStyle("supersuperclass");
					repo.joinNodes(supClassId, supsupClassId, "edge_" + supClassId + "_" + supsupClassId, "subclass of");

				}

			}

			for (OWLClass subclass : subclasses) {

				String subLabel = hpData.getExtOwlOntology().getLabel(subclass.getIRI());
				String subClassId = OboUtil.IRI2ID(subclass.getIRI());

				repo.addNode(subClassId, subLabel).setStyle("subclass");
				repo.joinNodes(subClassId, classId, "edge_" + subClassId + "_" + classId, "subclass of");

				ImmutableSet<OWLClass> subclasses2 = hpData.getExtOwlOntology().getChildren(subclass);

				for (OWLClass subSubClass : subclasses2) {
					String subsubLabel = hpData.getExtOwlOntology().getLabel(subSubClass.getIRI());
					String subsubClassId = OboUtil.IRI2ID(subSubClass.getIRI());

					repo.addNode(subsubClassId, subsubLabel).setStyle("subsubclass");
					repo.joinNodes(subsubClassId, subClassId, "edge_" + subsubClassId + "_" + subClassId, "subclass of");

				}

			}

		}
		else {

			// superclasses
			for (int i = 0; i < 3; i++) {
				String neighclassLabel = RandomStringUtils.randomAlphabetic(10) + "super";
				String neighclassId = "HP:" + RandomStringUtils.randomNumeric(7);
				repo.addNode(neighclassId, neighclassLabel).setStyle("superclass");
				repo.joinNodes(classId, neighclassId, "edge_" + neighclassId + "_" + classId, "subclass of");

				String neigh2classLabel = RandomStringUtils.randomAlphabetic(10) + "supersuper";
				String neigh2classId = "HP:" + RandomStringUtils.randomNumeric(7);
				repo.addNode(neigh2classId, neigh2classLabel).setStyle("supersuperclass");
				repo.joinNodes(neighclassId, neigh2classId, "edge2_" + neighclassId + "_" + classId, "subclass of");

			}

			// subclasses
			for (int i = 0; i < 7; i++) {
				String neighclassLabel = RandomStringUtils.randomAlphabetic(10);
				String neighclassId = "HP:" + RandomStringUtils.randomNumeric(7);
				repo.addNode(neighclassId, neighclassLabel).setStyle("subclass");
				repo.joinNodes(neighclassId, classId, "edge_" + classId + "_" + neighclassId, "subclass of");

				String neigh2classLabel = RandomStringUtils.randomAlphabetic(10);
				String neigh2classId = "HP:" + RandomStringUtils.randomNumeric(7);
				repo.addNode(neigh2classId, neigh2classLabel).setStyle("subsubclass");
				repo.joinNodes(neigh2classId, neighclassId, "edge2_" + neighclassId + "_" + classId, "subclass of");
			}

		}

		return repo;
	}
}
